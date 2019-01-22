package net.explorviz.landscape.repository;

import explorviz.live_trace_processing.reader.IPeriodicTimeSignalReceiver;
import explorviz.live_trace_processing.reader.TimeSignalReader;
import explorviz.live_trace_processing.record.IRecord;
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
import net.explorviz.landscape.repository.persistence.FstHelper;
import net.explorviz.landscape.repository.persistence.LandscapeRepository;
import net.explorviz.landscape.server.helper.LandscapeBroadcastService;
import net.explorviz.landscape.server.main.Configuration;
import net.explorviz.shared.annotations.Config;
import org.jvnet.hk2.annotations.Service;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Singleton
public final class LandscapeRepositoryModel implements IPeriodicTimeSignalReceiver {

  private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeRepositoryModel.class);

  private static final boolean LOAD_LAST_LANDSCAPE_ON_LOAD = false;
  private volatile Landscape lastPeriodLandscape;
  private final Landscape internalLandscape;
  private final FSTConfiguration fstConf;
  private final InsertionRepositoryPart insertionRepositoryPart;
  private final RemoteCallRepositoryPart remoteCallRepositoryPart;

  private final LandscapeBroadcastService broadcastService;

  @Config("repository.useDummyMode")
  private boolean useDummyMode;

  @Inject
  private LandscapeRepository<Landscape> landscapeRepository;

  @Inject
  public LandscapeRepositoryModel(final LandscapeBroadcastService broadcastService) {

    this.fstConf = this.initFSTConf();

    this.broadcastService = broadcastService;

    if (LOAD_LAST_LANDSCAPE_ON_LOAD) {

      // final Landscape readLandscape =
      // RepositoryFileStorage.readFromFile(System.currentTimeMillis(),
      // Configuration.LANDSCAPE_REPOSITORY);

      final Landscape readLandscape =
          this.landscapeRepository.getLandscapeByTimestamp(java.lang.System.currentTimeMillis());

      this.internalLandscape = readLandscape;
    } else {
      this.internalLandscape = new Landscape();
      this.internalLandscape.initializeId();
    }

    this.insertionRepositoryPart = new InsertionRepositoryPart();
    this.remoteCallRepositoryPart = new RemoteCallRepositoryPart();

    try {
      final Landscape l = this.fstConf.deepCopy(this.internalLandscape);
      this.lastPeriodLandscape = LandscapePreparer.prepareLandscape(l);
    } catch (final Exception e) {
      LOGGER.error("Error when deep-copying landscape.", e);
    }

    new TimeSignalReader(TimeUnit.SECONDS.toMillis(Configuration.outputIntervalSeconds), this)
        .start();
  }

  public FSTConfiguration initFSTConf() {
    return FstHelper.createFstConfiguration();
  }

  public Landscape getLastPeriodLandscape() {
    synchronized (this.lastPeriodLandscape) {
      return this.lastPeriodLandscape;
    }
  }

  public Landscape getLandscape(final long timestamp) {
    return LandscapePreparer
        .prepareLandscape(this.landscapeRepository.getLandscapeByTimestamp(timestamp));
  }

  public Landscape getReplay(final long timestamp) {
    return LandscapePreparer
        .prepareLandscape(this.landscapeRepository.getReplayByTimestamp(timestamp));
  }


  static {
    // TODO: Unused
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
   * Key function for the backend. Handles the persistence of a landscape every 10 seconds passed
   * timestamp format is nanoseconds since 1970, as defined in Kieker.
   */
  @Override
  public void periodicTimeSignal(final long timestamp) {
    synchronized (this.internalLandscape) {
      synchronized (this.lastPeriodLandscape) {

        final long milliseconds = java.lang.System.currentTimeMillis();

        // calculates the total requests for the internal landscape and stores them in its timestamp
        int calculatedTotalRequests = 0;

        if (this.useDummyMode) {
          final Landscape dummyLandscape = LandscapeDummyCreator.createDummyLandscape();
          dummyLandscape.getTimestamp().setTimestamp(milliseconds);
          dummyLandscape.getTimestamp().updateId();

          calculatedTotalRequests = calculateTotalRequests(dummyLandscape);
          dummyLandscape.getTimestamp().setTotalRequests(calculatedTotalRequests);

          this.landscapeRepository.saveLandscape(milliseconds, dummyLandscape);
          this.lastPeriodLandscape = dummyLandscape;
        } else {

          calculatedTotalRequests = calculateTotalRequests(this.internalLandscape);
          this.internalLandscape.getTimestamp().setTotalRequests(calculatedTotalRequests);
          this.internalLandscape.setTimestamp(new Timestamp(milliseconds, 0));

          this.landscapeRepository.saveLandscape(milliseconds, this.internalLandscape);
          try {
            final Landscape l = this.fstConf.deepCopy(this.internalLandscape);
            this.lastPeriodLandscape = LandscapePreparer.prepareLandscape(l);
          } catch (final Exception e) {
            LOGGER.error("Error when deep-copying landscape.", e);
          }
        }

        // broadcast latest landscape to registered clients
        this.broadcastService.broadcastMessage(this.lastPeriodLandscape);

        this.remoteCallRepositoryPart.checkForTimedoutRemoteCalls();
        this.resetCommunication();
      }
    }


    this.landscapeRepository.cleanup();
  }

  /**
   * Calculates all requests contained in a Landscape
   *
   * @param landscape
   */
  private static int calculateTotalRequests(final Landscape landscape) {

    int totalRequests = 0;

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
