package net.explorviz.settings.services.kafka;

import net.explorviz.settings.services.kafka.UserEventConsumer.UserEvent;

public interface UserEventHandler {


  void handle(UserEvent event);

}
