package net.explorviz.security.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import net.explorviz.security.services.KafkaUserService.UserEvent.EventType;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pushes user events to a kafka topic.
 */
@Service
public class KafkaUserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaUserService.class);

  private static final String TOPIC = "user-events";

  private final KafkaProducer<String, String> producer;


  @Inject
  public KafkaUserService(final KafkaProducer<String, String> producer) {
    this.producer = producer;
  }

  /**
   * Publish that a user has been deleted.
   *
   * @param deletedId the id of the created user
   * @throws JsonProcessingException if the event could not be serialized
   */
  public void publishDeleted(final String deletedId) throws JsonProcessingException {
    final UserEvent event = new UserEvent(EventType.DELETED, deletedId);
    final ObjectMapper om = new ObjectMapper();
    final String eventJson = om.writeValueAsString(event);

    final ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, eventJson);
    this.producer.send(record);

    LOGGER.info("Published deletion event to kafka");
  }

  /**
   * Publish that a user has been created.
   *
   * @param createdId the id of the created user
   * @throws JsonProcessingException if the event could not be serialized
   */
  public void publishCreated(final String createdId) throws JsonProcessingException {
    final UserEvent event = new UserEvent(EventType.DELETED, createdId);
    final ObjectMapper om = new ObjectMapper();
    final String eventJson = om.writeValueAsString(event);

    final ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, eventJson);
    this.producer.send(record);
    LOGGER.info("Published creation event to kafka");
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

  }

}
