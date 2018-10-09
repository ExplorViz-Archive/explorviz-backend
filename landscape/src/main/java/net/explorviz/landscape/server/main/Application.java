package net.explorviz.landscape.server.main;

import net.explorviz.landscape.server.exceptions.mapper.PathParamExceptionMapper;
import net.explorviz.landscape.server.exceptions.mapper.QueryParamExceptionMapper;
import net.explorviz.landscape.server.providers.CoreModelHandler;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.shared.exceptions.mapper.WebApplicationExceptionMapper;
import net.explorviz.shared.security.filters.AuthenticationFilter;
import net.explorviz.shared.security.filters.AuthorizationFilter;
import net.explorviz.shared.security.filters.CorsResponseFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Starting configuration for the backend - includes registring models, resources, exception
 * handers, providers, and embedds extensions.
 */
class Application extends ResourceConfig {
  public Application() {

    super();

    // register model types for JSONAPI provider
    CoreModelHandler.registerAllCoreModels();

    // https://stackoverflow.com/questions/30653012/
    // multipart-form-data-no-injection-source-found-for-a-parameter-of-type-public-ja/30656345
    // register for uploading landscapes
    this.register(MultiPartFeature.class);

    this.register(new DependencyInjectionBinder());
    // register(JacksonFeature)

    // register filters, e.g., authentication
    this.register(AuthenticationFilter.class);
    this.register(AuthorizationFilter.class);
    this.register(CorsResponseFilter.class);

    // resources
    this.packages("net.explorviz.server.resources");

    // exception handling (mind the order !)
    this.register(WebApplicationExceptionMapper.class);
    this.register(QueryParamExceptionMapper.class);
    this.register(PathParamExceptionMapper.class);
    this.register(GeneralExceptionMapper.class);

    this.register(SetupApplicationListener.class);

    // easy (de-)serializing models for HTTP Requests
    this.packages("net.explorviz.server.providers");

    // register extensions (For development: read plugin structure at github docs)
    this.packages("net.explorviz.extension");
  }
}
