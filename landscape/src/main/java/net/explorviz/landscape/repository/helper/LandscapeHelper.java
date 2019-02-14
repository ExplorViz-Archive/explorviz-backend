package net.explorviz.landscape.repository.helper;

import java.util.List;
import java.util.stream.Collectors;
import net.explorviz.shared.landscape.model.event.EEventType;
import net.explorviz.shared.landscape.model.event.Event;
import net.explorviz.shared.landscape.model.landscape.Landscape;

/**
 * Helper class providing methods for landscapes.
 */
public class LandscapeHelper {

  /**
   * Adds a new exception event to the list of events in the landscape
   *
   * @param landscape - related landscape
   * @param cause - cause of the exception
   */
  public static void addNewException(final Landscape landscape, final String cause) {
    long currentMillis = java.lang.System.currentTimeMillis();

    final List<Long> timestampsOfExceptionEvents =
        landscape.getEvents().stream().filter(e -> e.getEventType().equals(EEventType.EXCEPTION))
            .map(Event::getTimestamp).collect(Collectors.toList());

    while (timestampsOfExceptionEvents.contains(currentMillis)) {
      currentMillis++;
    }

    landscape.getEvents().add(new Event(currentMillis, EEventType.EXCEPTION, cause));
  }

  /**
   * Adds a new event to the list of events in the landscape
   *
   * @param landscape - related landscape
   * @param eventType - type of event
   * @param eventMesssage - message of the event
   */
  public static void addNewEvent(final Landscape landscape, final EEventType eventType,
      final String eventMesssage) {
    long currentMillis = java.lang.System.currentTimeMillis();

    final List<Long> timestampsOfEvents =
        landscape.getEvents().stream().filter(e -> !e.getEventType().equals(EEventType.EXCEPTION))
            .map(Event::getTimestamp).collect(Collectors.toList());

    while (timestampsOfEvents.contains(currentMillis)) {
      currentMillis++;
    }

    landscape.getEvents().add(new Event(currentMillis, eventType, eventMesssage));
  }


}
