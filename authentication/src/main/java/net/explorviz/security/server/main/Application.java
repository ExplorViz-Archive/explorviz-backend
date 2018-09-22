package net.explorviz.security.server.main;

import net.explorviz.security.server.filter.AuthenticationFilter;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.shared.exceptions.mapper.WebApplicationExceptionMapper;
import net.explorviz.shared.security.filters.CORSResponseFilter;
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
    register(new DependencyInjectionBinder());

    register(AuthenticationFilter.class);
    register(CORSResponseFilter.class);

    // exception handling (mind the order !)
    register(WebApplicationExceptionMapper.class);
    register(GeneralExceptionMapper.class);

    // register all resources in the given package
    packages("net.explorviz.security.server.resources");
  }
}
