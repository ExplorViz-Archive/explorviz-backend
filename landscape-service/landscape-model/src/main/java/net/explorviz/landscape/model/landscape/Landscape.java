package net.explorviz.landscape.model.landscape;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.application.ApplicationCommunication;
import net.explorviz.landscape.model.event.EEventType;
import net.explorviz.landscape.model.event.Event;
import net.explorviz.landscape.model.helper.BaseEntity;
import net.explorviz.landscape.model.store.Timestamp;

/**
 * Model representing a software landscape.
 */
@SuppressWarnings("serial")
@Type("landscape")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "super.id")
public class Landscape extends BaseEntity {


  @Relationship("timestamp")
  private Timestamp timestamp;

  @Relationship("systems")
  private final List<System> systems = new ArrayList<>();

  @Relationship("events")
  private final List<Event> events = new ArrayList<>();

  @Relationship("totalApplicationCommunications")
  private List<ApplicationCommunication> totalApplicationCommunications = new ArrayList<>();

  @JsonCreator
  public Landscape(@JsonProperty("id") final String id,
      @JsonProperty("timestamps") final Timestamp timestamp) {
    super(id);
    this.timestamp = timestamp;
  }

  public Timestamp getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(final Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  public List<System> getSystems() {
    return this.systems;
  }

  public List<Event> getEvents() {
    return this.events;
  }

  public List<ApplicationCommunication> getTotalApplicationCommunications() {
    return this.totalApplicationCommunications;
  }

  public void setTotalApplicationCommunications(
      final List<ApplicationCommunication> totalApplicationCommunications) {
    this.totalApplicationCommunications = totalApplicationCommunications;
  }

  /**
   * Clears all existing communication within the landscape.
   */
  private void clearCommunication() {

    // keeps applicationCommunication, but sets it to zero requests
    for (final ApplicationCommunication commu : this.getTotalApplicationCommunications()) {
      commu.reset();
    }

    for (final System system : this.getSystems()) {
      for (final NodeGroup nodegroup : system.getNodeGroups()) {
        for (final Node node : nodegroup.getNodes()) {
          for (final Application application : node.getApplications()) {
            application.clearCommunication();
          }
        }
      }
    }
  }

  /**
   * Resets the landscape.
   */
  public void reset() {
    this.getEvents().clear();
    this.clearCommunication();
  }

  /**
   * Creates a new exception event to the list of events in the landscape.
   *
   * @param id - if of related landscape
   * @param cause - cause of the exception
   */
  public void createNewException(final String id, final String cause) {
    long currentMillis = java.lang.System.currentTimeMillis();

    final List<Long> timestampsOfExceptionEvents =
        this.getEvents().stream().filter(e -> e.getEventType().equals(EEventType.EXCEPTION))
            .map(Event::getTimestamp).collect(Collectors.toList());

    while (timestampsOfExceptionEvents.contains(currentMillis)) {
      currentMillis++;
    }

    this.getEvents().add(new Event(id, currentMillis, EEventType.EXCEPTION, cause));
  }


  /**
   * Creates a new event to the list of events in the landscape.
   *
   * @param id - id of related landscape
   * @param eventType - type of event
   * @param eventMesssage - message of the event
   */
  public void createNewEvent(final String id, final EEventType eventType,
      final String eventMesssage) {
    long currentMillis = java.lang.System.currentTimeMillis();

    final List<Long> timestampsOfEvents =
        this.getEvents().stream().filter(e -> !e.getEventType().equals(EEventType.EXCEPTION))
            .map(Event::getTimestamp).collect(Collectors.toList());

    while (timestampsOfEvents.contains(currentMillis)) {
      currentMillis++;
    }

    this.getEvents().add(new Event(id, currentMillis, eventType, eventMesssage));
  }

  /**
   * Creates outgoing communication between applications in this landscape.
   */
  public void createOutgoingApplicationCommunication() {
    for (final ApplicationCommunication communication : this.getTotalApplicationCommunications()) {
      final Application sourceApp = communication.getSourceApplication();
      if (sourceApp != null) {
        sourceApp.getApplicationCommunications().add(communication);
      }
    }

  }

}
