package net.explorviz.settings.services.kafka;

import net.explorviz.settings.services.kafka.UserEventConsumer.UserEvent;
import net.explorviz.settings.services.kafka.UserEventConsumer.UserEvent.EventType;

public class UserDeletionHandler implements UserEventHandler {

  @Override
  public void handle(final UserEvent event) {
    if (event.getEvent() == EventType.DELETED) {

    }

  }



}
