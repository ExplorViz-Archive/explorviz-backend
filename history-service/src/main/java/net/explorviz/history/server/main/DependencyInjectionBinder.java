package net.explorviz.history.server.main;

import java.util.Properties;
import javax.inject.Singleton;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.history.repository.persistence.ReplayRepository;
import net.explorviz.history.repository.persistence.mongo.LandscapeSerializationHelper;
import net.explorviz.history.repository.persistence.mongo.MongoHelper;
import net.explorviz.history.repository.persistence.mongo.MongoLandscapeJsonApiRepository;
import net.explorviz.history.repository.persistence.mongo.MongoLandscapeRepository;
import net.explorviz.history.repository.persistence.mongo.MongoReplayJsonApiRepository;
import net.explorviz.history.repository.persistence.mongo.MongoReplayRepository;
import net.explorviz.shared.common.injection.CommonDependencyInjectionBinder;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.glassfish.hk2.api.TypeLiteral;

/**
 * Configures the dependency binding setup for inject during runtime.
 */
public class DependencyInjectionBinder extends CommonDependencyInjectionBinder {

  @Override
  public void configure() {

    super.configure();

    Properties properties = new Properties();
    properties.put("bootstrap.servers", "localhost:9092");
    properties.put("group.id", "test");
    properties.put("enable.auto.commit", "true");
    properties.put("auto.commit.interval.ms", "1000");
    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    properties.put("value.deserializer",
        "org.apache.kafka.common.serialization.StringDeserializer");

    this.bind(new KafkaConsumer<String, String>(properties))
        .to(new TypeLiteral<KafkaConsumer<String, String>>() {});

    // Persistence
    this.bind(MongoHelper.class).to(MongoHelper.class).in(Singleton.class);
    this.bind(MongoLandscapeJsonApiRepository.class).to(MongoLandscapeJsonApiRepository.class)
        .in(Singleton.class);
    this.bind(LandscapeSerializationHelper.class).to(LandscapeSerializationHelper.class)
        .in(Singleton.class);
    // Landscape
    this.bind(MongoLandscapeRepository.class).to(MongoLandscapeRepository.class)
        .in(Singleton.class);
    this.bind(MongoLandscapeJsonApiRepository.class).to(MongoLandscapeJsonApiRepository.class)
        .in(Singleton.class);
    this.bind(MongoLandscapeRepository.class)
        .to(new TypeLiteral<LandscapeRepository<Landscape>>() {}).in(Singleton.class);
    this.bind(MongoLandscapeJsonApiRepository.class)
        .to(new TypeLiteral<LandscapeRepository<String>>() {}).in(Singleton.class);
    // Replay
    this.bind(MongoReplayRepository.class).to(MongoReplayRepository.class).in(Singleton.class);
    this.bind(MongoReplayJsonApiRepository.class).to(MongoReplayJsonApiRepository.class)
        .in(Singleton.class);
    this.bind(MongoReplayRepository.class).to(new TypeLiteral<ReplayRepository<Landscape>>() {})
        .in(Singleton.class);
    this.bind(MongoReplayJsonApiRepository.class).to(new TypeLiteral<ReplayRepository<String>>() {})
        .in(Singleton.class);

  }
}
