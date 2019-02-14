package net.explorviz.landscape.repository.helper;

import net.explorviz.shared.landscape.model.event.EEventType;
import net.explorviz.shared.landscape.model.event.Event;
import net.explorviz.shared.landscape.model.landscape.Landscape;

/**
 * Helper class providing methods for manipulating landscapes.
 */
public class LandscapeHelper {

  /**
   * Creates a new event which occurs in the landscape during monitoring to the landscape
   *
   * @param landscape - the related landscape
   * @param timestamp - timestamp the event occurred
   * @param eventType - the type of the event
   * @param eventMessage - a concrete message for the user
   */
  public static void createEvent(final Landscape landscape, final EEventType eventType,
      final String eventMessage) {
    final long currentMillis = java.lang.System.currentTimeMillis();
    final Event newEvent = new Event(landscape, currentMillis, eventType, eventMessage);
    landscape.getEvents().add(newEvent);
  }

}
