package net.explorviz.broadcast.server.main;

import javax.inject.Singleton;
import net.explorviz.broadcast.kafka.KafkaLandscapeExchangeService;
import net.explorviz.broadcast.kafka.LandscapeSerializationHelper;
import net.explorviz.broadcast.server.helper.LandscapeBroadcastService;
import net.explorviz.shared.common.injection.CommonDependencyInjectionBinder;

/**
 * Configures the dependency binding setup for inject during runtime.
 */
public class DependencyInjectionBinder extends CommonDependencyInjectionBinder {

  @Override
  public void configure() {

    super.configure();

    this.bind(LandscapeSerializationHelper.class)
        .to(LandscapeSerializationHelper.class)
        .in(Singleton.class);

    // Broadcast Mechanism
    this.bind(LandscapeBroadcastService.class)
        .to(LandscapeBroadcastService.class)
        .in(Singleton.class);

    this.bind(KafkaLandscapeExchangeService.class)
        .to(KafkaLandscapeExchangeService.class)
        .in(Singleton.class);
  }
}
