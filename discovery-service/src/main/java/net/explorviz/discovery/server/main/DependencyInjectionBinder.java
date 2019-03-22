package net.explorviz.discovery.server.main;

import javax.inject.Singleton;
import net.explorviz.discovery.repository.discovery.AgentRepository;
import net.explorviz.discovery.server.resources.AgentBroadcastSubResource;
import net.explorviz.discovery.server.services.BroadcastService;
import net.explorviz.shared.common.injection.CommonDependencyInjectionBinder;
import net.explorviz.shared.discovery.services.ClientService;

/**
 * Configures the dependency binding setup for inject during runtime.
 */
public class DependencyInjectionBinder extends CommonDependencyInjectionBinder {

  @Override
  public void configure() {

    super.configure();

    // Discovery Mechanism
    this.bind(ClientService.class).to(ClientService.class).in(Singleton.class);
    this.bind(AgentRepository.class).to(AgentRepository.class).in(Singleton.class);

    // Broadcast Mechanism
    this.bind(BroadcastService.class).to(BroadcastService.class).in(Singleton.class);
    this.bind(AgentBroadcastSubResource.class).to(AgentBroadcastSubResource.class);
  }
}
