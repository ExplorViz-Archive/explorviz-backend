package net.explorviz.server.main;

import com.github.jasminb.jsonapi.ResourceConverter;
import javax.inject.Singleton;
import net.explorviz.api.ExtensionApiImpl;
import net.explorviz.discovery.services.ClientService;
import net.explorviz.repository.LandscapeExchangeService;
import net.explorviz.repository.LandscapeRepositoryModel;
import net.explorviz.repository.discovery.AgentRepository;
import net.explorviz.server.helper.BroadcastService;
import net.explorviz.server.injection.ResourceConverterFactory;
import net.explorviz.server.resources.LandscapeBroadcastSubResource;
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

    // Broadcast Mechanism
    this.bind(BroadcastService.class).to(BroadcastService.class).in(Singleton.class);
    this.bind(LandscapeBroadcastSubResource.class).to(LandscapeBroadcastSubResource.class);

    // injectable config properties
    this.bind(new ConfigInjectionResolver()).to(new TypeLiteral<InjectionResolver<Config>>() {});

    // ErrorObject Handler
    this.bind(ErrorObjectHelper.class).to(ErrorObjectHelper.class).in(Singleton.class);

    // Discovery Mechanism
    this.bind(ClientService.class).to(ClientService.class).in(Singleton.class);
    this.bind(AgentRepository.class).to(AgentRepository.class).in(Singleton.class);
  }
}
