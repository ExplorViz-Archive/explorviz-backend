package net.explorviz.landscape.server.main;

import java.util.Properties;
import javax.inject.Singleton;
import net.explorviz.landscape.repository.LandscapeRepositoryModel;
import net.explorviz.landscape.repository.helper.LandscapeSerializationHelper;
import net.explorviz.landscape.server.helper.LandscapeBroadcastService;
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

    final Properties properties = new Properties();
    properties.put("bootstrap.servers", "localhost:9092");
    properties.put("acks", "all");
    properties.put("retries", "1");
    properties.put("batch.size", "16384");
    properties.put("linger.ms", "1");
    properties.put("max.request.size", "2097152");
    properties.put("buffer.memory", 33554432);
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    this.bind(new KafkaProducer<String, String>(properties))
        .to(new TypeLiteral<KafkaProducer<String, String>>() {});

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
