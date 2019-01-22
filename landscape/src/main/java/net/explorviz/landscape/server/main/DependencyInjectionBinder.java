package net.explorviz.landscape.server.main;

import com.github.jasminb.jsonapi.ResourceConverter;
import javax.inject.Singleton;
import net.explorviz.landscape.api.ExtensionApiImpl;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.repository.LandscapeExchangeService;
import net.explorviz.landscape.repository.LandscapeRepositoryModel;
import net.explorviz.landscape.repository.persistence.LandscapeRepository;
import net.explorviz.landscape.repository.persistence.mongo.LandscapeSerializationHelper;
import net.explorviz.landscape.repository.persistence.mongo.MongoHelper;
import net.explorviz.landscape.repository.persistence.mongo.MongoJsonApiRepository;
import net.explorviz.landscape.repository.persistence.mongo.MongoRepository;
import net.explorviz.landscape.server.helper.BroadcastService;
import net.explorviz.landscape.server.injection.ResourceConverterFactory;
import net.explorviz.landscape.server.resources.LandscapeBroadcastSubResource;
import net.explorviz.shared.annotations.Config;
import net.explorviz.shared.annotations.injection.ConfigInjectionResolver;
import net.explorviz.shared.exceptions.ErrorObjectHelper;
import net.explorviz.shared.security.TokenParserService;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Configures the dependency binding setup for inject during runtime.
 */
public class DependencyInjectionBinder extends AbstractBinder {

  @Override
  public void configure() {
    this.bind(LandscapeRepositoryModel.class).to(LandscapeRepositoryModel.class)
        .in(Singleton.class);
    this.bind(LandscapeExchangeService.class).to(LandscapeExchangeService.class)
        .in(Singleton.class);

    this.bindFactory(ResourceConverterFactory.class).to(ResourceConverter.class)
        .in(Singleton.class);

    this.bind(ExtensionApiImpl.class).to(ExtensionApiImpl.class).in(Singleton.class);

    this.bind(TokenParserService.class).to(TokenParserService.class).in(Singleton.class);

    // Persistence
    this.bind(MongoHelper.class).to(MongoHelper.class).in(Singleton.class);
    this.bind(MongoJsonApiRepository.class).to(MongoJsonApiRepository.class).in(Singleton.class);
    this.bind(LandscapeSerializationHelper.class).to(LandscapeSerializationHelper.class)
        .in(Singleton.class);
    this.bind(MongoRepository.class).to(MongoRepository.class).in(Singleton.class);
    this.bind(MongoRepository.class).to(new TypeLiteral<LandscapeRepository<Landscape>>() {});
    this.bind(MongoJsonApiRepository.class).to(new TypeLiteral<LandscapeRepository<String>>() {});



    // Broadcast Mechanism
    this.bind(LandscapeBroadcastService.class).to(LandscapeBroadcastService.class)
        .in(Singleton.class);
    this.bind(LandscapeBroadcastSubResource.class).to(LandscapeBroadcastSubResource.class);

    // injectable config properties
    this.bind(new ConfigInjectionResolver()).to(new TypeLiteral<InjectionResolver<Config>>() {
    });

    // ErrorObject Handler
    this.bind(ErrorObjectHelper.class).to(ErrorObjectHelper.class).in(Singleton.class);
  }
}
