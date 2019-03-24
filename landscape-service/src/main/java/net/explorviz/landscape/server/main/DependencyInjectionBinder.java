package net.explorviz.landscape.server.main;

import javax.inject.Singleton;
import net.explorviz.landscape.api.ExtensionApiImpl;
import net.explorviz.landscape.repository.LandscapeExchangeService;
import net.explorviz.landscape.repository.LandscapeRepositoryModel;
import net.explorviz.landscape.repository.persistence.LandscapeRepository;
import net.explorviz.landscape.repository.persistence.ReplayRepository;
import net.explorviz.landscape.repository.persistence.mongo.LandscapeSerializationHelper;
import net.explorviz.landscape.repository.persistence.mongo.MongoHelper;
import net.explorviz.landscape.repository.persistence.mongo.MongoLandscapeJsonApiRepository;
import net.explorviz.landscape.repository.persistence.mongo.MongoLandscapeRepository;
import net.explorviz.landscape.repository.persistence.mongo.MongoReplayJsonApiRepository;
import net.explorviz.landscape.repository.persistence.mongo.MongoReplayRepository;
import net.explorviz.landscape.server.helper.LandscapeBroadcastService;
import net.explorviz.landscape.server.resources.LandscapeBroadcastSubResource;
import net.explorviz.shared.common.injection.CommonDependencyInjectionBinder;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import org.glassfish.hk2.api.TypeLiteral;

/**
 * Configures the dependency binding setup for inject during runtime.
 */
public class DependencyInjectionBinder extends CommonDependencyInjectionBinder {

  @Override
  public void configure() {

    // TODO read properties file service.generator.redis=boolean

    // this.bind(RedisServiceIdGenerator.class).to(ServiceIdGenerator.class).in(Singleton.class);

    super.configure();

    this.bind(LandscapeRepositoryModel.class).to(LandscapeRepositoryModel.class)
        .in(Singleton.class);
    this.bind(LandscapeExchangeService.class).to(LandscapeExchangeService.class)
        .in(Singleton.class);

    this.bind(ExtensionApiImpl.class).to(ExtensionApiImpl.class).in(Singleton.class);

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

    // Broadcast Mechanism
    this.bind(LandscapeBroadcastService.class).to(LandscapeBroadcastService.class)
        .in(Singleton.class);
    this.bind(LandscapeBroadcastSubResource.class).to(LandscapeBroadcastSubResource.class);
  }
}
