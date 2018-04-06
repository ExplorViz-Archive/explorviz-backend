package net.explorviz.server.main;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import net.explorviz.server.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.server.exceptions.mapper.PathParamExceptionMapper;
import net.explorviz.server.exceptions.mapper.QueryParamExceptionMapper;
import net.explorviz.server.providers.CoreModelHandler;

/**
 * Starting configuration for the backend - includes registring models,
 * resources, exception handers, providers, and embedds extensions
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
@ApplicationPath("")
class ExplorViz extends ResourceConfig {
	public ExplorViz() {

		// register model types for JSONAPI provider
		CoreModelHandler.registerAllCoreModels();

		register(new DependencyInjectionBinder());
		// register(JacksonFeature)
		// Authentication & Authorization
		packages("net.explorviz.server.filters");

		// resources
		packages("net.explorviz.server.resources");

		// exception handling (mind the order !)
		register(QueryParamExceptionMapper.class);
		register(PathParamExceptionMapper.class);
		register(GeneralExceptionMapper.class);

		// easy (de-)serializing models for HTTP Requests
		packages("net.explorviz.server.providers");

		// register extensions (For development: read plugin structure at github docs)
		packages("net.explorviz.extension");
	}
}
