package net.explorviz.landscape.repository;

import explorviz.live_trace_processing.reader.IPeriodicTimeSignalReceiver;
import explorviz.live_trace_processing.reader.TimeSignalReader;
import explorviz.live_trace_processing.record.IRecord;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.store.Timestamp;
import net.explorviz.landscape.repository.persistence.FstHelper;
import net.explorviz.landscape.repository.persistence.LandscapeRepository;
import net.explorviz.landscape.server.helper.BroadcastService;
import net.explorviz.landscape.server.main.Configuration;
import net.explorviz.shared.annotations.Config;
import org.jvnet.hk2.annotations.Service;
import org.nustaq.serialization.FSTConfiguration;

@Service
@Singleton
public final class LandscapeRepositoryModel implements IPeriodicTimeSignalReceiver {

  private static final boolean LOAD_LAST_LANDSCAPE_ON_LOAD = false;
  private volatile Landscape lastPeriodLandscape;
  private final Landscape internalLandscape;
  private final FSTConfiguration fstConf;
  private final InsertionRepositoryPart insertionRepositoryPart;
  private final RemoteCallRepositoryPart remoteCallRepositoryPart;

  private final BroadcastService broadcastService;

  @Config("repository.useDummyMode")
  private boolean useDummyMode;

  @Inject
  private LandscapeRepository landscapeRepository;

  @Inject
  public LandscapeRepositoryModel(final BroadcastService broadcastService) {

    this.broadcastService = broadcastService;

    this.fstConf = this.initFstConf();

    if (LOAD_LAST_LANDSCAPE_ON_LOAD) {

      // final Landscape readLandscape =
      // RepositoryFileStorage.readFromFile(System.currentTimeMillis(),
      // Configuration.LANDSCAPE_REPOSITORY);

      final Landscape readLandscape =
          this.landscapeRepository.getLandscapeByTimestamp(System.currentTimeMillis());

      this.internalLandscape = readLandscape;
    } else {
      this.internalLandscape = new Landscape();
      this.internalLandscape.initializeId();
    }

    this.insertionRepositoryPart = new InsertionRepositoryPart();
    this.remoteCallRepositoryPart = new RemoteCallRepositoryPart();

    final Landscape l = this.fstConf.deepCopy(this.internalLandscape);

    this.lastPeriodLandscape = LandscapePreparer.prepareLandscape(l);

    new TimeSignalReader(TimeUnit.SECONDS.toMillis(Configuration.outputIntervalSeconds), this)
        .start();
  }

  public Landscape getLastPeriodLandscape() {
    synchronized (this.lastPeriodLandscape) {
      return this.lastPeriodLandscape;
    }
  }

  public Landscape getLandscape(final long timestamp, final String folderName)
      throws FileNotFoundException {
    return LandscapePreparer
        .prepareLandscape(this.landscapeRepository.getLandscapeByTimestamp(timestamp));

    // RepositoryFileStorage.readFromFile(timestamp, folderName)
  }

  // TODO: Unused?
  public Map<Long, Long> getAvailableLandscapes(final String folderName) {
    // return RepositoryFileStorage.getAvailableModelsForTimeshift(folderName);
    return this.landscapeRepository.getAllForTimeshift();
  }

  static {
    Configuration.DATABASE_NAMES.add("hsqldb");
    Configuration.DATABASE_NAMES.add("postgres");
    Configuration.DATABASE_NAMES.add("db2");
    Configuration.DATABASE_NAMES.add("mysql");
    Configuration.DATABASE_NAMES.add("neo4j");
    Configuration.DATABASE_NAMES.add("database");
    Configuration.DATABASE_NAMES.add("hypersql");
  }

  public FSTConfiguration initFstConf() {
    return FstHelper.createFstConfiguration();
  }


  public void reset() {
    synchronized (this.internalLandscape) {
      this.internalLandscape.reset();
    }
  }

  /**
   * Gets called each 10 seconds by external explorviz library.
   */
  @Override
  public void periodicTimeSignal(final long timestamp) {
    // called every tenth second
    // passed timestamp is nanosecond
    synchronized (this.internalLandscape) {
      synchronized (this.lastPeriodLandscape) {

        final long milliseconds = System.currentTimeMillis();

        if (this.useDummyMode) {
          final Landscape dummyLandscape = LandscapeDummyCreator.createDummyLandscape();
          dummyLandscape.getTimestamp().setTimestamp(milliseconds);
          dummyLandscape.getTimestamp().updateId();

          this.landscapeRepository.saveLandscape(milliseconds, dummyLandscape);
          this.lastPeriodLandscape = dummyLandscape;
        } else {
          this.internalLandscape.updateTimestamp(new Timestamp(milliseconds, 0));

          this.landscapeRepository.saveLandscape(milliseconds, this.internalLandscape);
          final Landscape l = this.fstConf.deepCopy(this.internalLandscape);
          this.lastPeriodLandscape = LandscapePreparer.prepareLandscape(l);
        }

        // broadcast to registered clients
        this.broadcastService.broadcastMessage(this.lastPeriodLandscape);

        this.remoteCallRepositoryPart.checkForTimedoutRemoteCalls();
        this.resetCommunication();
      }
    }


    this.landscapeRepository.cleanup();
  }

  private void resetCommunication() {
    this.internalLandscape.reset();
  }

  public void insertIntoModel(final IRecord inputIRecord) {
    // called every second
    this.insertionRepositoryPart.insertIntoModel(inputIRecord, this.internalLandscape,
        this.remoteCallRepositoryPart);
  }
}
