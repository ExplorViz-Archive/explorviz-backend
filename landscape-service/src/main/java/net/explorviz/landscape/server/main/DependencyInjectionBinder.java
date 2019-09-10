package net.explorviz.landscape.server.main;

import javax.inject.Singleton;
import net.explorviz.landscape.injection.KafkaProducerFactory;
import net.explorviz.landscape.repository.LandscapeRepositoryModel;
import net.explorviz.landscape.repository.helper.LandscapeSerializationHelper;
import net.explorviz.shared.common.idgen.RedisServiceIdGenerator;
import net.explorviz.shared.common.idgen.ServiceIdGenerator;
import net.explorviz.shared.common.injection.CommonDependencyInjectionBinder;
import net.explorviz.shared.config.helper.PropertyHelper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.glassfish.hk2.api.TypeLiteral;

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
          .ranked(1000); // NOCS
    }

    this.bindFactory(KafkaProducerFactory.class)
        .to(new TypeLiteral<KafkaProducer<String, String>>() {});

    this.bind(LandscapeSerializationHelper.class)
        .to(LandscapeSerializationHelper.class)
        .in(Singleton.class);

    this.bind(LandscapeRepositoryModel.class)
        .to(LandscapeRepositoryModel.class)
        .in(Singleton.class);

  }
}
