package net.explorviz.history.kafka;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import javax.inject.Inject;
import net.explorviz.history.repository.persistence.mongo.LandscapeSerializationHelper;
import net.explorviz.history.repository.persistence.mongo.MongoLandscapeJsonApiRepository;
import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaLandscapeExchangeService implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaLandscapeExchangeService.class);

  private final KafkaConsumer<String, String> kafkaConsumer;

  private final LandscapeSerializationHelper serializationHelper;

  private final MongoLandscapeJsonApiRepository mongoLandscapeRepo;

  private final String kafkaTopic;

  @Inject
  public KafkaLandscapeExchangeService(LandscapeSerializationHelper serializationHelper,
      MongoLandscapeJsonApiRepository mongoLandscapeRepo,
      @Config("exchange.kafka.topic.name") String kafkaTopic,
      @Config("exchange.kafka.group.id") String kafkaGroupId,
      @Config("exchange.kafka.bootstrap.servers") String kafkaBootStrapServerList) {

    this.serializationHelper = serializationHelper;
    this.mongoLandscapeRepo = mongoLandscapeRepo;
    this.kafkaTopic = kafkaTopic;

    Properties properties = new Properties();
    properties.put("bootstrap.servers", kafkaBootStrapServerList);
    properties.put("group.id", kafkaGroupId);
    properties.put("enable.auto.commit", "true");
    properties.put("auto.commit.interval.ms", "1000");
    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    properties.put("value.deserializer",
        "org.apache.kafka.common.serialization.StringDeserializer");

    this.kafkaConsumer = new KafkaConsumer<String, String>(properties);
  }

  @Override
  public void run() {
    LOGGER.info("Starting Kafka Exchange \n");

    kafkaConsumer.subscribe(Arrays.asList(kafkaTopic));

    while (true) {
      ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));

      for (ConsumerRecord<String, String> record : records) {

        LOGGER.debug("Recevied landscape Kafka record: {}", record.value());

        String serializedLandscape = record.value();

        Landscape l;
        try {
          l = this.serializationHelper.deserialize(serializedLandscape);
        } catch (DocumentSerializationException e) {
          LOGGER.error("Could not deserialize landscape with value {}", serializedLandscape, e);
          continue;
        }

        mongoLandscapeRepo.save(l.getTimestamp().getTimestamp(), l,
            l.getTimestamp().getTotalRequests());
      }
    }

  }

}
