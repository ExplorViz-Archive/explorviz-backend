package net.explorviz.security.server.main;

import net.explorviz.security.server.filter.AuthenticationFilter;
import net.explorviz.security.server.providers.JsonApiListProvider;
import net.explorviz.security.server.providers.JsonApiProvider;
import net.explorviz.security.server.providers.ResourceConverterFactory;
import net.explorviz.security.server.resources.RoleResource;
import net.explorviz.security.server.resources.TokenResource;
import net.explorviz.security.server.resources.UserResource;
import net.explorviz.security.server.resources.UserSettingsResource;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.shared.exceptions.mapper.WebApplicationExceptionMapper;
import net.explorviz.shared.security.filters.CorsResponseFilter;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * JAX-RS application. This class is responsible for registering all types of classes, e.g.,
 * resource classes or exception mappers
 */
public class Application extends ResourceConfig {

  /**
   * JAX-RS application. This class is responsible for registering all types of classes, e.g.,
   * resource classes or exception mappers
   */
  public Application() { // NOPMD

    // register CDI
    this.register(new DependencyInjectionBinder());

    this.register(AuthenticationFilter.class);
    this.register(CorsResponseFilter.class);

    this.register(net.explorviz.shared.security.filters.AuthenticationFilter.class);
    this.register(net.explorviz.shared.security.filters.AuthorizationFilter.class);

    // exception handling (mind the order !)
    this.register(WebApplicationExceptionMapper.class);
    this.register(GeneralExceptionMapper.class);

    this.register(SetupApplicationListener.class);

    this.register(JsonApiProvider.class);
    this.register(JsonApiListProvider.class);
    this.register(ResourceConverterFactory.class);

    // register all resources
    this.register(TokenResource.class);
    this.register(UserResource.class);
    this.register(RoleResource.class);
    this.register(UserSettingsResource.class);
  }



}
