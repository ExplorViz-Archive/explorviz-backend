package net.explorviz.landscape.server.main;

import javax.inject.Singleton;
import net.explorviz.landscape.repository.LandscapeRepositoryModel;
import net.explorviz.landscape.repository.helper.LandscapeSerializationHelper;
import net.explorviz.landscape.server.helper.LandscapeBroadcastService;
import net.explorviz.shared.common.idgen.RedisServiceIdGenerator;
import net.explorviz.shared.common.idgen.ServiceIdGenerator;
import net.explorviz.shared.common.injection.CommonDependencyInjectionBinder;
import net.explorviz.shared.config.helper.PropertyHelper;

/**
 * Configures the dependency binding setup for inject during runtime.
 */
public class DependencyInjectionBinder extends CommonDependencyInjectionBinder {

  @Override
  public void configure() {

    super.configure();

    final boolean useRedisForIdGeneration =
        PropertyHelper.getBooleanProperty("service.generator.id.redis");

    if (useRedisForIdGeneration) {
      this.bind(RedisServiceIdGenerator.class)
          .to(ServiceIdGenerator.class)
          .in(Singleton.class)
          .ranked(1000);
    }

    this.bind(LandscapeSerializationHelper.class)
        .to(LandscapeSerializationHelper.class)
        .in(Singleton.class);

    this.bind(LandscapeRepositoryModel.class)
        .to(LandscapeRepositoryModel.class)
        .in(Singleton.class);

    // Broadcast Mechanism
    this.bind(LandscapeBroadcastService.class)
        .to(LandscapeBroadcastService.class)
        .in(Singleton.class);
  }
}
