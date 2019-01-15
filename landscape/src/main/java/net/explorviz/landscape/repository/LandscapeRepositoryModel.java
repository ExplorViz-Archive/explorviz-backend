package net.explorviz.landscape.repository;

import explorviz.live_trace_processing.reader.IPeriodicTimeSignalReceiver;
import explorviz.live_trace_processing.reader.TimeSignalReader;
import explorviz.live_trace_processing.record.IRecord;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.explorviz.landscape.model.application.AggregatedClazzCommunication;
import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.application.ApplicationCommunication;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.landscape.Node;
import net.explorviz.landscape.model.landscape.NodeGroup;
import net.explorviz.landscape.model.landscape.System;
import net.explorviz.landscape.model.store.Timestamp;
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
  public LandscapeRepositoryModel(final BroadcastService broadcastService) {

    this.broadcastService = broadcastService;

    this.fstConf = this.initFstConf();

    if (LOAD_LAST_LANDSCAPE_ON_LOAD) {

      final Landscape readLandscape = RepositoryStorage
          .readFromFile(java.lang.System.currentTimeMillis(), Configuration.LANDSCAPE_REPOSITORY);

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
        .prepareLandscape(RepositoryStorage.readFromFile(timestamp, folderName));
  }

  public Map<Long, Long> getAvailableLandscapes(final String folderName) {
    return RepositoryStorage.getAvailableModelsForTimeshift(folderName);
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
    return RepositoryStorage.createFstConfiguration();
  }

  public void reset() {
    synchronized (this.internalLandscape) {
      this.internalLandscape.reset();
    }
  }

  @Override
  public void periodicTimeSignal(final long timestamp) {
    // called every tenth second
    // passed timestamp is in nanosecond
    synchronized (this.internalLandscape) {
      synchronized (this.lastPeriodLandscape) {

        final long milliseconds = java.lang.System.currentTimeMillis();

        // calculates the total requests for the internal landscape and stores them in its timestamp
        final long calculatedTotalRequests = calculateTotalRequests(this.internalLandscape);
        this.internalLandscape.getTimestamp().setTotalRequests(calculatedTotalRequests);

        if (this.useDummyMode) {
          final Landscape dummyLandscape = LandscapeDummyCreator.createDummyLandscape();
          dummyLandscape.getTimestamp().setTimestamp(milliseconds);
          dummyLandscape.getTimestamp().updateId();
          RepositoryStorage.writeToFile(dummyLandscape, milliseconds, calculatedTotalRequests,
              Configuration.LANDSCAPE_REPOSITORY);
          this.lastPeriodLandscape = dummyLandscape;
        } else {
          this.internalLandscape.updateTimestamp(new Timestamp(milliseconds, 0));
          RepositoryStorage.writeToFile(this.internalLandscape, milliseconds,
              calculatedTotalRequests, Configuration.LANDSCAPE_REPOSITORY);
          final Landscape l = this.fstConf.deepCopy(this.internalLandscape);
          this.lastPeriodLandscape = LandscapePreparer.prepareLandscape(l);
        }

        // broadcast to registered clients
        this.broadcastService.broadcastMessage(this.lastPeriodLandscape);

        this.remoteCallRepositoryPart.checkForTimedoutRemoteCalls();
        this.resetCommunication();
      }
    }

    RepositoryStorage.cleanUpTooOldFiles(java.lang.System.currentTimeMillis(),
        Configuration.LANDSCAPE_REPOSITORY);
  }

  /**
   * Calculates all requests contained in a Landscape
   *
   * @param landscape
   */
  private static long calculateTotalRequests(final Landscape landscape) {

    long totalRequests = 0;

    for (final System system : landscape.getSystems()) {
      for (final NodeGroup nodegroup : system.getNodeGroups()) {
        for (final Node node : nodegroup.getNodes()) {
          for (final Application application : node.getApplications()) {
            // aggClazzCommunication
            for (final AggregatedClazzCommunication clazzCommu : application
                .getAggregatedClazzCommunications()) {
              totalRequests += clazzCommu.getTotalRequests();

            }
            // applicationCommunication
            for (final ApplicationCommunication appCommu : application
                .getApplicationCommunications()) {
              totalRequests += appCommu.getRequests();
            }
          }
        }
      }
    }
    return totalRequests;
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
