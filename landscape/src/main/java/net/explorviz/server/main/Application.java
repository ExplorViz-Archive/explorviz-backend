package net.explorviz.server.main;

import net.explorviz.server.exceptions.mapper.PathParamExceptionMapper;
import net.explorviz.server.exceptions.mapper.QueryParamExceptionMapper;
import net.explorviz.server.providers.CoreModelHandler;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.shared.exceptions.mapper.WebApplicationExceptionMapper;
import net.explorviz.shared.security.filters.AuthenticationFilter;
import net.explorviz.shared.security.filters.AuthorizationFilter;
import net.explorviz.shared.security.filters.CORSResponseFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Starting configuration for the backend - includes registring models, resources, exception
 * handers, providers, and embedds extensions.
 */
class Application extends ResourceConfig {
  public Application() {

    // register model types for JSONAPI provider
    CoreModelHandler.registerAllCoreModels();

    // https://stackoverflow.com/questions/30653012/multipart-form-data-no-injection-source-found-for-a-parameter-of-type-public-ja/30656345
    // register for uploading landscapes
    register(MultiPartFeature.class);

    register(new DependencyInjectionBinder());
    // register(JacksonFeature)

    // register filters, e.g., authentication
    register(AuthenticationFilter.class);
    register(AuthorizationFilter.class);
    register(CORSResponseFilter.class);

    // resources
    packages("net.explorviz.server.resources");

    // exception handling (mind the order !)
    register(WebApplicationExceptionMapper.class);
    register(QueryParamExceptionMapper.class);
    register(PathParamExceptionMapper.class);
    register(GeneralExceptionMapper.class);

    register(SetupApplicationListener.class);

    // easy (de-)serializing models for HTTP Requests
    packages("net.explorviz.server.providers");

    // register extensions (For development: read plugin structure at github docs)
    packages("net.explorviz.extension");
  }
}
