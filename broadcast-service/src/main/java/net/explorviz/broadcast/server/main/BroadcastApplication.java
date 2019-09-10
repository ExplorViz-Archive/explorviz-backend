package net.explorviz.broadcast.server.main;

import net.explorviz.broadcast.server.resources.LandscapeBroadcastResource;
import net.explorviz.landscape.model.helper.TypeProvider;
import net.explorviz.shared.common.provider.GenericTypeFinder;
import net.explorviz.shared.common.provider.JsonApiListProvider;
import net.explorviz.shared.common.provider.JsonApiProvider;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.shared.exceptions.mapper.InvalidJsonApiResourceExceptionMapper;
import net.explorviz.shared.exceptions.mapper.UnregisteredTypeExceptionMapper;
import net.explorviz.shared.exceptions.mapper.WebApplicationExceptionMapper;
import net.explorviz.shared.security.filters.AuthenticationFilter;
import net.explorviz.shared.security.filters.AuthorizationFilter;
import net.explorviz.shared.security.filters.CorsResponseFilter;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Starting configuration for this service - includes registring models, resources, exception
 * handers, providers, and embedds extensions.
 */
public class BroadcastApplication extends ResourceConfig {

  /**
   * Starting configuration for this service - includes registring models, resources, exception
   * handers, providers, and embedds extensions.
   */
  public BroadcastApplication() {

    super();

    // register Landscape Model classes, since we want to use them
    TypeProvider.getExplorVizCoreTypesAsMap().forEach((classname, classRef) -> {
      GenericTypeFinder.getTypeMap().put(classname, classRef);
    });

    this.register(new DependencyInjectionBinder());

    // easy (de-)serializing models for HTTP Requests
    this.register(JsonApiProvider.class);
    this.register(JsonApiListProvider.class);

    // register filters, e.g., authentication
    this.register(AuthenticationFilter.class);
    this.register(AuthorizationFilter.class);
    this.register(CorsResponseFilter.class);

    // resources
    this.register(LandscapeBroadcastResource.class);

    // exception handling (mind the order !)
    this.register(InvalidJsonApiResourceExceptionMapper.class);
    this.register(UnregisteredTypeExceptionMapper.class);
    this.register(WebApplicationExceptionMapper.class);
    this.register(GeneralExceptionMapper.class);

    this.register(SetupApplicationListener.class);
    // swagger
    this.packages("io.swagger.v3.jaxrs2.integration.resources");
  }
}
