package net.explorviz.discovery.server.main;

import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.SerializationFeature;
import javax.inject.Singleton;
import net.explorviz.discovery.model.Agent;
import net.explorviz.discovery.model.Procezz;
import net.explorviz.discovery.repository.discovery.AgentRepository;
import net.explorviz.discovery.server.resources.AgentBroadcastSubResource;
import net.explorviz.discovery.server.services.BroadcastService;
import net.explorviz.discovery.services.ClientService;
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

    // Prepare JSON-API resource converter
    final ResourceConverter resourceConverter = new ResourceConverter();
    resourceConverter.registerType(Agent.class);
    resourceConverter.registerType(Procezz.class);
    resourceConverter
        .enableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES);

    this.bind(resourceConverter).to(ResourceConverter.class);

    this.bind(TokenParserService.class).to(TokenParserService.class).in(Singleton.class);

    // injectable config properties
    this.bind(new ConfigInjectionResolver()).to(new TypeLiteral<InjectionResolver<Config>>() {});

    // ErrorObject Handler
    this.bind(ErrorObjectHelper.class).to(ErrorObjectHelper.class).in(Singleton.class);

    // Discovery Mechanism
    this.bind(ClientService.class).to(ClientService.class).in(Singleton.class);
    this.bind(AgentRepository.class).to(AgentRepository.class).in(Singleton.class);

    // Broadcast Mechanism
    this.bind(BroadcastService.class).to(BroadcastService.class).in(Singleton.class);
    this.bind(AgentBroadcastSubResource.class).to(AgentBroadcastSubResource.class);
  }
}
