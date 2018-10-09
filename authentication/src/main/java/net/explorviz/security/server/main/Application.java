package net.explorviz.security.server.main;

import net.explorviz.security.server.filter.AuthenticationFilter;
import net.explorviz.security.server.providers.JsonApiListProvider;
import net.explorviz.security.server.providers.JsonApiProvider;
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

    // exception handling (mind the order !)
    this.register(WebApplicationExceptionMapper.class);
    this.register(GeneralExceptionMapper.class);

    // (un-)marshaling
    this.register(JsonApiListProvider.class);
    this.register(JsonApiProvider.class);


    // register all resources in the given package
    this.packages("net.explorviz.security.server.resources");
  }
}
