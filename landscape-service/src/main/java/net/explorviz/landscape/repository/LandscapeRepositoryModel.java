package net.explorviz.landscape.repository;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import explorviz.live_trace_processing.reader.IPeriodicTimeSignalReceiver;
import explorviz.live_trace_processing.reader.TimeSignalReader;
import explorviz.live_trace_processing.record.IRecord;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.explorviz.landscape.repository.helper.DummyLandscapeHelper;
import net.explorviz.landscape.repository.persistence.LandscapeRepository;
import net.explorviz.landscape.repository.persistence.ReplayRepository;
import net.explorviz.landscape.repository.persistence.mongo.LandscapeSerializationHelper;
import net.explorviz.landscape.server.helper.LandscapeBroadcastService;
import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.landscape.model.application.AggregatedClazzCommunication;
import net.explorviz.shared.landscape.model.application.Application;
import net.explorviz.shared.landscape.model.application.ApplicationCommunication;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.landscape.model.landscape.Node;
import net.explorviz.shared.landscape.model.landscape.NodeGroup;
import net.explorviz.shared.landscape.model.landscape.System;
import net.explorviz.shared.landscape.model.store.Timestamp;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Singleton
public final class LandscapeRepositoryModel implements IPeriodicTimeSignalReceiver {

  private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeRepositoryModel.class);

  private static final boolean LOAD_LAST_LANDSCAPE_ON_LOAD = false;
  private volatile Landscape lastPeriodLandscape;
  private Landscape internalLandscape;
  private final InsertionRepositoryPart insertionRepositoryPart;
  private final RemoteCallRepositoryPart remoteCallRepositoryPart;
  private final int outputIntervalSeconds;

  private final LandscapeBroadcastService broadcastService;

  private final LandscapeRepository<Landscape> landscapeRepository;

  private final ReplayRepository<Landscape> replayRepository;

  private final LandscapeSerializationHelper serializationHelper;

  private final boolean useDummyMode;


  @Inject
  public LandscapeRepositoryModel(final LandscapeBroadcastService broadcastService,
      final LandscapeRepository<Landscape> landscapeRepository,
      final ReplayRepository<Landscape> replayRepository,
      final LandscapeSerializationHelper serializationHelper,
      @Config("repository.outputIntervalSeconds") final int outputIntervalSeconds,
      @Config("repository.useDummyMode") final boolean useDummyMode) {

    this.broadcastService = broadcastService;
    this.landscapeRepository = landscapeRepository;
    this.replayRepository = replayRepository;
    this.serializationHelper = serializationHelper;

    this.useDummyMode = useDummyMode;
    this.insertionRepositoryPart = new InsertionRepositoryPart();
    this.remoteCallRepositoryPart = new RemoteCallRepositoryPart();
    this.outputIntervalSeconds = outputIntervalSeconds;
  }

  @PostConstruct
  public void init() {
    if (LOAD_LAST_LANDSCAPE_ON_LOAD) {

      // final Landscape readLandscape =
      // RepositoryFileStorage.readFromFile(System.currentTimeMillis(),
      // Configuration.LANDSCAPE_REPOSITORY);

      final Landscape readLandscape =
          this.landscapeRepository.getByTimestamp(java.lang.System.currentTimeMillis());

      this.internalLandscape = readLandscape;
    } else {
      this.internalLandscape = new Landscape();
    }

    try {
      final Landscape l = this.deepCopy(this.internalLandscape);
      l.createOutgoingApplicationCommunication();
      this.lastPeriodLandscape = l;
    } catch (final Exception e) {
      LOGGER.error("Error when deep-copying landscape.", e);
    }

    new TimeSignalReader(TimeUnit.SECONDS.toMillis(this.outputIntervalSeconds), this).start();
  }


  public Landscape getLastPeriodLandscape() {
    synchronized (this.lastPeriodLandscape) {
      return this.lastPeriodLandscape;
    }
  }

  public Landscape getLandscape(final long timestamp) {
    final Landscape l = this.landscapeRepository.getByTimestamp(timestamp);
    l.createOutgoingApplicationCommunication();
    return l;
  }

  public Landscape getReplay(final long timestamp) {
    final Landscape l = this.replayRepository.getByTimestamp(timestamp);
    l.createOutgoingApplicationCommunication();
    return l;
  }


  public void reset() {
    synchronized (this.internalLandscape) {
      this.internalLandscape.reset();
    }
  }

  private Landscape deepCopy(final Landscape original) throws DocumentSerializationException {
    final String serialized = this.serializationHelper.serialize(original);
    return this.serializationHelper.deserialize(serialized);
  }

  /**
   * Key function for the backend. Handles the persistence of a landscape every 10 seconds passed
   * timestamp format is 'milliseconds' since 1970, as defined in Kieker.
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

          calculatedTotalRequests = DummyLandscapeHelper.getRandomNum(500, 25000);
          dummyLandscape.getTimestamp().setTotalRequests(calculatedTotalRequests);

          this.landscapeRepository.save(milliseconds, dummyLandscape, calculatedTotalRequests);
          this.lastPeriodLandscape = dummyLandscape;
        } else {

          calculatedTotalRequests = calculateTotalRequests(this.internalLandscape);
          this.internalLandscape.getTimestamp().setTotalRequests(calculatedTotalRequests);
          this.internalLandscape.setTimestamp(new Timestamp(milliseconds, calculatedTotalRequests));

          this.landscapeRepository
              .save(milliseconds, this.internalLandscape, calculatedTotalRequests);
          try {
            final Landscape l = this.deepCopy(this.internalLandscape);
            l.createOutgoingApplicationCommunication();
            this.lastPeriodLandscape = l;
          } catch (final Exception e) {
            LOGGER.error("Error while deep-copying landscape.", e);
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
   * Calculates all requests contained in a Landscape.
   *
   * @param landscape the landscape
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
    this.insertionRepositoryPart
        .insertIntoModel(inputIRecord, this.internalLandscape, this.remoteCallRepositoryPart);
  }
}
