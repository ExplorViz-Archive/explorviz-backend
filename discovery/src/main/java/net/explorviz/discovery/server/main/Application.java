package net.explorviz.discovery.server.main;

import net.explorviz.discovery.server.providers.JsonApiListProvider;
import net.explorviz.discovery.server.providers.JsonApiProvider;
import net.explorviz.discovery.server.resources.AgentResource;
import net.explorviz.discovery.server.resources.ProcezzResource;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.shared.exceptions.mapper.WebApplicationExceptionMapper;
import net.explorviz.shared.security.filters.CorsResponseFilter;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Starting configuration for the backend - includes registring models, resources, exception
 * handers, providers, and embedds extensions.
 */
class Application extends ResourceConfig {
  public Application() {

    super();

    this.register(new DependencyInjectionBinder());

    // register filters, e.g., authentication
    // this.register(AuthenticationFilter.class);
    // this.register(AuthorizationFilter.class);
    this.register(CorsResponseFilter.class);

    // resources
    this.register(AgentResource.class);
    this.register(ProcezzResource.class);

    // exception handling (mind the order !)
    this.register(WebApplicationExceptionMapper.class);
    this.register(GeneralExceptionMapper.class);

    this.register(SetupApplicationListener.class);

    // easy (de-)serializing models for HTTP Requests
    this.register(JsonApiProvider.class);
    this.register(JsonApiListProvider.class);
  }
}
