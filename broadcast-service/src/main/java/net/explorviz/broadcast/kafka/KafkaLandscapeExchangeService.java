package net.explorviz.broadcast.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import javax.inject.Inject;
import net.explorviz.broadcast.server.helper.LandscapeBroadcastService;
import net.explorviz.shared.config.annotations.Config;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exchange service for consuming landscape objects via Kafka topics. Started by @see
 * SetupApplicationListener on application startup.
 *
 */
@Service
public class KafkaLandscapeExchangeService implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaLandscapeExchangeService.class);

  private final KafkaConsumer<String, String> kafkaConsumer;

  private final LandscapeBroadcastService broadcastService;

  private final String kafkaTopic;

  /**
   * Exchange service for consuming landscape objects via Kafka topics. Started by @see
   * SetupApplicationListener on application startup.
   *
   */
  @Inject
  public KafkaLandscapeExchangeService(final LandscapeBroadcastService broadcastService,
      @Config("exchange.kafka.topic.name") final String kafkaTopic,
      @Config("exchange.kafka.group.id") final String kafkaGroupId,
      @Config("exchange.kafka.bootstrap.servers") final String kafkaBootStrapServerList) {

    this.broadcastService = broadcastService;

    this.kafkaTopic = kafkaTopic;

    final Properties properties = new Properties();
    properties.put("bootstrap.servers", kafkaBootStrapServerList);
    properties.put("group.id", kafkaGroupId);
    properties.put("enable.auto.commit", "true");
    properties.put("auto.commit.interval.ms", "1000");
    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");// NOCS
    properties.put("value.deserializer",
        "org.apache.kafka.common.serialization.StringDeserializer");

    this.kafkaConsumer = new KafkaConsumer<>(properties);
  }

  @Override
  public void run() {
    LOGGER.info("Starting Kafka Exchange \n");

    this.kafkaConsumer.subscribe(Arrays.asList(this.kafkaTopic));

    while (true) {
      final ConsumerRecords<String, String> records =
          this.kafkaConsumer.poll(Duration.ofMillis(100));

      for (final ConsumerRecord<String, String> record : records) {

        LOGGER.debug("Recevied landscape Kafka record: {}", record.value());

        final String serializedLandscape = record.value();

        // broadcast latest landscape to registered clients
        this.broadcastService.broadcastMessage(serializedLandscape);
      }
    }

  }

}
