package net.explorviz.landscape.repository;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import explorviz.live_trace_processing.reader.TimeSignalReader;
import explorviz.live_trace_processing.record.IRecord;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.repository.LandscapeRepositoryModel;
import net.explorviz.landscape.repository.helper.DummyLandscapeHelper;
import net.explorviz.landscape.repository.helper.LandscapeDummyCreator;
import net.explorviz.landscape.repository.helper.LandscapeSerializationHelper;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.config.annotations.Config;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LandscapeRepositoryModelDummy implements LandscapeRepositoryModel {

  private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeRepositoryModelDummy.class);

  private final int outputIntervalSeconds;
  private final LandscapeSerializationHelper serializationHelper;
  private final KafkaProducer<String, String> kafkaProducer;

  private final String kafkaTopicName;

  private final IdGenerator idGen;

  @Inject
  public LandscapeRepositoryModelDummy(final LandscapeSerializationHelper serializationHelper,
      final KafkaProducer<String, String> kafkaProducer, final IdGenerator idGen,
      @Config("repository.outputIntervalSeconds") final int outputIntervalSeconds,
      @Config("exchange.kafka.topic.name") final String kafkaTopicName) {

    this.serializationHelper = serializationHelper;
    this.kafkaProducer = kafkaProducer;
    this.idGen = idGen;

    this.outputIntervalSeconds = outputIntervalSeconds;
    this.kafkaTopicName = kafkaTopicName;

    new TimeSignalReader(TimeUnit.SECONDS.toMillis(this.outputIntervalSeconds), this).start();
    LOGGER.info("Started Landscaper-Service in dummy mode");
  }


  @Override public void periodicTimeSignal(long timestamp) {
    int calculatedTotalRequests = 0;
    final long milliseconds = java.lang.System.currentTimeMillis();
    final Landscape dummyLandscape = LandscapeDummyCreator.createDummyLandscape(this.idGen);
    dummyLandscape.getTimestamp().setTimestamp(milliseconds);
    dummyLandscape.getTimestamp().setId(this.idGen.generateId());
    calculatedTotalRequests = DummyLandscapeHelper.getRandomNum(500, 25000);
    dummyLandscape.getTimestamp().setTotalRequests(calculatedTotalRequests);
    this.sendLandscapeToKafka(dummyLandscape, this.kafkaTopicName);
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

  @Override public void insertIntoModel(IRecord inputIRecord) {

  }
}
