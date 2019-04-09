package net.explorviz.history.server.main;

import net.explorviz.shared.common.provider.JsonApiListProvider;
import net.explorviz.shared.common.provider.JsonApiProvider;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.shared.exceptions.mapper.WebApplicationExceptionMapper;
import net.explorviz.shared.security.filters.AuthenticationFilter;
import net.explorviz.shared.security.filters.AuthorizationFilter;
import net.explorviz.shared.security.filters.CorsResponseFilter;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Starting configuration for the backend - includes registring models, resources, exception
 * handers, providers, and embedds extensions.
 */
class Application extends ResourceConfig {

  public Application() {

    this.register(new DependencyInjectionBinder());

    // register filters, e.g., authentication
    this.register(AuthenticationFilter.class);
    this.register(AuthorizationFilter.class);
    this.register(CorsResponseFilter.class);

    // exception handling (mind the order !)
    this.register(WebApplicationExceptionMapper.class);
    this.register(GeneralExceptionMapper.class);

    this.register(SetupApplicationListener.class);

    // easy (de-)serializing models for HTTP Requests
    this.register(JsonApiProvider.class);
    this.register(JsonApiListProvider.class);
  }
}
