package net.explorviz.discovery.server.main;

import net.explorviz.discovery.server.resources.AgentResource;
import net.explorviz.discovery.server.resources.ProcezzResource;
import net.explorviz.shared.common.provider.GenericTypeFinder;
import net.explorviz.shared.common.provider.JsonApiListProvider;
import net.explorviz.shared.common.provider.JsonApiProvider;
import net.explorviz.shared.discovery.model.Agent;
import net.explorviz.shared.discovery.model.Procezz;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.shared.exceptions.mapper.InvalidJsonApiResourceExceptionMapper;
import net.explorviz.shared.exceptions.mapper.UnregisteredTypeExceptionMapper;
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

    GenericTypeFinder.getTypeMap().put("Agent", Agent.class);
    GenericTypeFinder.getTypeMap().put("Procezz", Procezz.class);

    this.register(new DependencyInjectionBinder());

    // register filters, e.g., authentication
    this.register(AuthenticationFilter.class);
    this.register(AuthorizationFilter.class);
    this.register(CorsResponseFilter.class);

    // resources
    this.register(AgentResource.class);
    this.register(ProcezzResource.class);

    // exception handling (mind the order !)
    this.register(InvalidJsonApiResourceExceptionMapper.class);
    this.register(UnregisteredTypeExceptionMapper.class);
    this.register(WebApplicationExceptionMapper.class);
    this.register(GeneralExceptionMapper.class);

    this.register(SetupApplicationListener.class);

    // easy (de-)serializing models for HTTP Requests
    this.register(JsonApiProvider.class);
    this.register(JsonApiListProvider.class);

    // swagger
    this.packages("io.swagger.v3.jaxrs2.integration.resources");
  }
}
