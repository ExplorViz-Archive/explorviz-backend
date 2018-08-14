package net.explorviz.server.main;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.github.jasminb.jsonapi.ResourceConverter;

import net.explorviz.api.ExtensionAPIImpl;
import net.explorviz.discovery.services.ClientService;
import net.explorviz.model.helper.ErrorObjectHelper;
import net.explorviz.repository.LandscapeExchangeService;
import net.explorviz.repository.LandscapeRepositoryModel;
import net.explorviz.repository.discovery.AgentRepository;
import net.explorviz.server.helper.BroadcastService;
import net.explorviz.server.injection.ResourceConverterFactory;
import net.explorviz.server.resources.LandscapeBroadcastSubResource;
import net.explorviz.shared.security.TokenParserService;

/**
 * Configures the dependency binding setup for inject during runtime
 */
public class DependencyInjectionBinder extends AbstractBinder {

	@Override
	public void configure() {
		this.bind(LandscapeRepositoryModel.class).to(LandscapeRepositoryModel.class).in(Singleton.class);
		this.bind(LandscapeExchangeService.class).to(LandscapeExchangeService.class).in(Singleton.class);

		this.bindFactory(ResourceConverterFactory.class).to(ResourceConverter.class).in(Singleton.class);

		this.bind(ExtensionAPIImpl.class).to(ExtensionAPIImpl.class).in(Singleton.class);

		this.bind(TokenParserService.class).to(TokenParserService.class).in(Singleton.class);

		this.bind(ErrorObjectHelper.class).to(ErrorObjectHelper.class).in(Singleton.class);

		// Broadcast Mechanism
		this.bind(BroadcastService.class).to(BroadcastService.class).in(Singleton.class);
		this.bind(LandscapeBroadcastSubResource.class).to(LandscapeBroadcastSubResource.class).in(Singleton.class);

		// ErrorObject Handler
		this.bind(ErrorObjectHelper.class).to(ErrorObjectHelper.class).in(Singleton.class);

		// Discovery Mechanism
		this.bind(ClientService.class).to(ClientService.class).in(Singleton.class);
		this.bind(AgentRepository.class).to(AgentRepository.class).in(Singleton.class);
	}
}