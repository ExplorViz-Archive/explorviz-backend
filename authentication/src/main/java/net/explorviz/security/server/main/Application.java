package net.explorviz.security.server.main;

import net.explorviz.security.server.filter.AuthenticationFilter;
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

    // (un-)marshaling
    this.packages("net.explorviz.security.server.providers");



    // register all resources in the given package
    this.packages("net.explorviz.security.server.resources");
  }
}
