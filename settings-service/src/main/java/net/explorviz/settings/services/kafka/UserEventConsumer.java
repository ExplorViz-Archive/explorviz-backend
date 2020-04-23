package net.explorviz.settings.services.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
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
 * Consumes user events published on Kafka.
 */
@Service
public class UserEventConsumer implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserEventConsumer.class);
  private static final String TOPIC = "user-events";


  private final KafkaConsumer<String, String> kafkaConsumer;

  private UserEventHandler handler;


  /**
   * Creates a new consumer that ingests user events from kafka once run.
   *
   * @param kafkaBootStrapServerList the bootstrap server
   * @param groupId                  the kafka group id to use
   */
  public UserEventConsumer(
      @Config("exchange.kafka.bootstrap.servers") final String kafkaBootStrapServerList,
      @Config("exchange.kafka.group.id") final String groupId) {

    final Properties properties = new Properties();
    properties.put("bootstrap.servers", kafkaBootStrapServerList);
    properties.put("group.id", groupId);
    properties.put("enable.auto.commit", "true");
    properties.put("auto.commit.interval.ms", "1000");
    properties
        .put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");// NOCS
    properties.put("value.deserializer",
        "org.apache.kafka.common.serialization.StringDeserializer");

    this.kafkaConsumer = new KafkaConsumer<>(properties);
  }

  public void setHandler(final UserEventHandler handler) {
    this.handler = handler;
  }

  @Override
  public void run() {
    LOGGER.info("Starting Kafka Exchange \n");

    this.kafkaConsumer.subscribe(Collections.singletonList(TOPIC));
    final ObjectMapper mapper = new ObjectMapper();
    while (true) {
      final ConsumerRecords<String, String> records =
          this.kafkaConsumer.poll(Duration.ofMillis(100));

      for (final ConsumerRecord<String, String> record : records) {

        LOGGER.debug("Recevied landscape Kafka record: {}", record.value());

        final String serializedUserEvent = record.value();
        UserEvent e;
        try {
          e = mapper.readValue(serializedUserEvent, UserEvent.class);
          if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Received User Event: " + e.toString());
          }
          this.notifyHandler(e);
        } catch (final IOException ex) {
          if (LOGGER.isErrorEnabled()) {
            LOGGER.error("Could not deserialize value", ex);
          }
        }
      }
    }
  }

  private void notifyHandler(final UserEvent event) {
    if (this.handler != null) {
      switch (event.getEvent()) {
        case CREATED:
          this.handler.onCreate(event.getId());
          break;
        case DELETED:
          this.handler.onDelete(event.getId());
          break;
        default:
          if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("Received unknown user event: {}", event.getEvent());
          }
          break;
      }
    }
  }

  /**
   * User events read from Kafka topic.
   */
  public static class UserEvent {

    /**
     * Denotes which type of event occurred for the user of given id.
     */
    public enum EventType {
      CREATED, DELETED;
    }


    @JsonProperty("event")
    private EventType event;

    @JsonProperty("id")
    private String id;

    public UserEvent() { // NOPMD
      /* Jackson */
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
