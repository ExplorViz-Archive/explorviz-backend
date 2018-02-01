package net.explorviz.server.main

import javax.ws.rs.ApplicationPath
import net.explorviz.server.exceptions.mapper.GeneralExceptionMapper
import net.explorviz.server.exceptions.mapper.PathParamExceptionMapper
import net.explorviz.server.exceptions.mapper.QueryParamExceptionMapper
import org.glassfish.jersey.server.ResourceConfig
import net.explorviz.server.providers.CoreModelHandler

@ApplicationPath("")
class ExplorViz extends ResourceConfig {
	new() {

		// register model types for JSONAPI provider
		CoreModelHandler.registerAllCoreModels();

		register(new DependencyInjectionBinder())
		// register(JacksonFeature)
		// Authentication & Authorization
		packages("net.explorviz.server.filters")

		// resources
		packages("net.explorviz.server.resources")

		// exception handling (mind the order !)
		register(QueryParamExceptionMapper)
		register(PathParamExceptionMapper)
		register(GeneralExceptionMapper)

		// easy (de-)serializing models for HTTP Requests
		packages("net.explorviz.server.providers")

		// register extensions (For development: read plugin structure at github docs)
		packages("net.explorviz.extension")

	}
}
