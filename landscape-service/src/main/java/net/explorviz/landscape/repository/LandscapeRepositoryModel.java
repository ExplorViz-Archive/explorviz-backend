package net.explorviz.landscape.repository;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import explorviz.live_trace_processing.reader.IPeriodicTimeSignalReceiver;
import explorviz.live_trace_processing.reader.TimeSignalReader;
import explorviz.live_trace_processing.record.IRecord;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
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
import net.explorviz.landscape.repository.helper.LandscapeSerializationHelper;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.config.annotations.Config;
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

  private final LandscapeSerializationHelper serializationHelper;


  private final KafkaProducer<String, String> kafkaProducer;

  private final String kafkaTopicName;

  private final IdGenerator idGen;

  @Inject
  public LandscapeRepositoryModel(final LandscapeSerializationHelper serializationHelper,
      final KafkaProducer<String, String> kafkaProducer, final IdGenerator idGen,
      @Config("repository.outputIntervalSeconds") final int outputIntervalSeconds,
      @Config("exchange.kafka.topic.name") final String kafkaTopicName) {

    this.serializationHelper = serializationHelper;
    this.kafkaProducer = kafkaProducer;
    this.idGen = idGen;

    this.insertionRepositoryPart = new InsertionRepositoryPart(idGen);
    this.remoteCallRepositoryPart = new RemoteCallRepositoryPart();
    this.outputIntervalSeconds = outputIntervalSeconds;
    this.kafkaTopicName = kafkaTopicName;
  }

  @PostConstruct
  public void init() {

    this.internalLandscape = new Landscape(this.idGen.generateId(),
        new Timestamp(this.idGen.generateId(), java.lang.System.currentTimeMillis(), 0));

    try {
      final Landscape l = this.deepCopy(this.internalLandscape);
      l.createOutgoingApplicationCommunication();
      this.lastPeriodLandscape = l;
    } catch (final DocumentSerializationException e) {
      LOGGER.error("Error when deep-copying landscape.", e);
    }

    new TimeSignalReader(TimeUnit.SECONDS.toMillis(this.outputIntervalSeconds), this).start();
  }

  private Landscape deepCopy(final Landscape original) throws DocumentSerializationException {
    final String serialized = this.serializationHelper.serialize(original);
    return this.serializationHelper.deserialize(serialized);
  }

  /**
   * Key functionality in the backend. Handles the persistence of a landscape every 10 seconds
   * passed. The employed time unit is defined as following in the Kieker configuration file
   * (monitoring.properties):
   *
   * TimeSource: 'kieker.monitoring.timer.SystemNanoTimer' Time in nanoseconds (with nanoseconds
   * precision) since Thu Jan 01 01:00:00 CET 1970'
   */
  @Override
  public void periodicTimeSignal(final long timestamp) {
    synchronized (this.internalLandscape) {
      synchronized (this.lastPeriodLandscape) {

        final long milliseconds = java.lang.System.currentTimeMillis();

        // calculates the total requests for the internal landscape and stores them in its timestamp
        int calculatedTotalRequests = 0;

        calculatedTotalRequests = calculateTotalRequests(this.internalLandscape);
        this.internalLandscape.getTimestamp().setTotalRequests(calculatedTotalRequests);
        this.internalLandscape.setTimestamp(
            new Timestamp(this.idGen.generateId(), milliseconds, calculatedTotalRequests));

        this.internalLandscape.setId(this.idGen.generateId());

        this.sendLandscapeToKafka(this.internalLandscape, this.kafkaTopicName);

        try {
          final Landscape l = this.deepCopy(this.internalLandscape);
          l.createOutgoingApplicationCommunication();
          this.lastPeriodLandscape = l;
        } catch (final DocumentSerializationException e) {
          LOGGER.error("Error while deep-copying landscape.", e);
        }

        this.remoteCallRepositoryPart.checkForTimedoutRemoteCalls();
        this.resetCommunication();
      }
    }
  }

  /**
   * Sends a landscape object with all its relationships to a Kafka topic.
   *
   * @param l that should be send to the Kafka topic
   */
  private void sendLandscapeToKafka(final Landscape l, final String kafkaTopicName) {
    try {
      final String serialized = this.serializationHelper.serialize(l);
      this.kafkaProducer.send(new ProducerRecord<>(kafkaTopicName, "1", serialized));
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Sending Kafka record with landscape id {}, timestamp {}, and payload to topic {}",
            l.getId(),
            l.getTimestamp().getTimestamp(),
            kafkaTopicName);
      }
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
