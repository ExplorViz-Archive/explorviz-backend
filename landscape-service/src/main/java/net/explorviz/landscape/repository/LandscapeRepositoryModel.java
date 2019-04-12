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
import net.explorviz.landscape.repository.helper.LandscapeSerializationHelper;
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
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Singleton
public final class LandscapeRepositoryModel implements IPeriodicTimeSignalReceiver {

  private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeRepositoryModel.class);

  private volatile Landscape lastPeriodLandscape;
  private Landscape internalLandscape;
  private final InsertionRepositoryPart insertionRepositoryPart;
  private final RemoteCallRepositoryPart remoteCallRepositoryPart;
  private final int outputIntervalSeconds;

  private final LandscapeBroadcastService broadcastService;
  private final LandscapeSerializationHelper serializationHelper;

  private final boolean useDummyMode;

  private final KafkaProducer<String, String> kafkaProducer;

  private final String kafkaTopicName;


  @Inject
  public LandscapeRepositoryModel(final LandscapeBroadcastService broadcastService,
      final LandscapeSerializationHelper serializationHelper,
      final KafkaProducer<String, String> kafkaProducer,
      @Config("repository.outputIntervalSeconds") final int outputIntervalSeconds,
      @Config("repository.useDummyMode") final boolean useDummyMode,
      @Config("service.kafka.topic.name") final String kafkaTopicName) {

    this.broadcastService = broadcastService;
    this.serializationHelper = serializationHelper;
    this.kafkaProducer = kafkaProducer;
    this.useDummyMode = useDummyMode;
    this.insertionRepositoryPart = new InsertionRepositoryPart();
    this.remoteCallRepositoryPart = new RemoteCallRepositoryPart();
    this.outputIntervalSeconds = outputIntervalSeconds;
    this.kafkaTopicName = kafkaTopicName;
  }

  @PostConstruct
  public void init() {

    this.internalLandscape = new Landscape();

    try {
      final Landscape l = this.deepCopy(this.internalLandscape);
      l.createOutgoingApplicationCommunication();
      this.lastPeriodLandscape = l;
    } catch (final Exception e) {
      LOGGER.error("Error when deep-copying landscape.", e);
    }

    new TimeSignalReader(TimeUnit.SECONDS.toMillis(this.outputIntervalSeconds), this).start();
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

          this.sendLandscapeToKafka(dummyLandscape, this.kafkaTopicName);

          this.lastPeriodLandscape = dummyLandscape;
        } else {

          calculatedTotalRequests = calculateTotalRequests(this.internalLandscape);
          this.internalLandscape.getTimestamp().setTotalRequests(calculatedTotalRequests);
          this.internalLandscape.setTimestamp(new Timestamp(milliseconds, calculatedTotalRequests));

          this.sendLandscapeToKafka(this.internalLandscape, this.kafkaTopicName);

          try {
            final Landscape l = this.deepCopy(this.internalLandscape);
            l.createOutgoingApplicationCommunication();
            this.lastPeriodLandscape = l;
          } catch (final DocumentSerializationException e) {
            LOGGER.error("Error while deep-copying landscape.", e);
          }
        }

        // broadcast latest landscape to registered clients
        this.broadcastService.broadcastMessage(this.lastPeriodLandscape);

        this.remoteCallRepositoryPart.checkForTimedoutRemoteCalls();
        this.resetCommunication();
      }
    }
  }

  /**
   * Sends a landscape object with all its relationships to a Kafka topic.
   *
   * @param Landscape that should be send to the Kafka topic
   */
  private void sendLandscapeToKafka(final Landscape l, final String kafkaTopicName) {
    try {
      final String serialized = this.serializationHelper.serialize(l);
      this.kafkaProducer.send(new ProducerRecord<>(kafkaTopicName, "1", serialized));
      LOGGER.info(
          "Sending Kafka record with landscape id {}, timestamp {}, and payload to topic {}",
          l.getId(),
          l.getTimestamp().getTimestamp(),
          kafkaTopicName);
    } catch (final DocumentSerializationException e) {
      LOGGER.error("Could not serialize landscape to string for Kafka Production.", e);
    }
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
