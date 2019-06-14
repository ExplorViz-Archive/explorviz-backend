package net.explorviz.settings.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import net.explorviz.shared.config.annotations.Config;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consumes User Events published on Kafka
 *
 */
@Service
public class UserEventListener implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserEventListener.class);
  private static final String TOPIC = "user-events";
  private final KafkaConsumer<String, String> kafkaConsumer;

  public UserEventListener(
      @Config("exchange.kafka.bootstrap.servers") final String kafkaBootStrapServerList,
      @Config("exchange.kafka.group.id") final String groupId) {

    final Properties properties = new Properties();
    properties.put("bootstrap.servers", kafkaBootStrapServerList);
    properties.put("group.id", groupId);
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

    this.kafkaConsumer.subscribe(Arrays.asList(TOPIC));

    while (true) {
      final ConsumerRecords<String, String> records =
          this.kafkaConsumer.poll(Duration.ofMillis(100));

      for (final ConsumerRecord<String, String> record : records) {

        LOGGER.debug("Recevied landscape Kafka record: {}", record.value());

        final String serializedUserEvent = record.value();

        final ObjectMapper mapper = new ObjectMapper();
        UserEvent e = null;
        try {
          e = mapper.readValue(serializedUserEvent, UserEvent.class);
          LOGGER.info("Received User Event: " + e.toString());
        } catch (final IOException e1) {
          LOGGER.error("Could not deserialize value");
        }


      }
    }

  }

  static class UserEvent {

    public enum EventType {
      CREATED, DELETED;
    }

    @JsonProperty("event")
    private final EventType event;

    @JsonProperty("id")
    private final String id;

    public UserEvent(final EventType event, final String id) {
      this.event = event;
      this.id = id;
    }

    public EventType getEvent() {
      return this.event;
    }

    public String getId() {
      return this.id;
    }

    @Override
    public String toString() {
      return new ToStringBuilder(this).append(this.event).append(this.id).toString();
    }

  }

}
