package net.explorviz.landscape.model.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.annotations.Type;
import net.explorviz.landscape.model.helper.BaseEntity;
import net.explorviz.landscape.model.landscape.Landscape;

/**
 * Model representing an event occurring in a {@link Landscape}.
 */
@SuppressWarnings("serial")
@Type("event")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "super.id")
public class Event extends BaseEntity {

  private long timestamp;

  private EEventType eventType = EEventType.UNKNOWN;

  private String eventMessage;

  @JsonCreator
  public Event(@JsonProperty("id") final String id, @JsonProperty("timestamp") final long timestamp,
      @JsonProperty("eventType") final EEventType eventType,
      @JsonProperty("eventMessage") final String eventMessage) {
    super(id);
    this.timestamp = timestamp;
    this.eventType = eventType;
    this.eventMessage = eventMessage;
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(final long timestamp) {
    this.timestamp = timestamp;
  }

  public EEventType getEventType() {
    return this.eventType;
  }

  public void setEventType(final EEventType eventType) {
    this.eventType = eventType;
  }

  public String getEventMessage() {
    return this.eventMessage;
  }

  public void setEventMessage(final String eventMessage) {
    this.eventMessage = eventMessage;
  }

}
