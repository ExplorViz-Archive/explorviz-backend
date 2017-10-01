package net.explorviz.server.main

import net.explorviz.server.exceptions.mapper.GeneralExceptionMapper
import net.explorviz.server.exceptions.mapper.PathParamExceptionMapper
import net.explorviz.server.exceptions.mapper.QueryParamExceptionMapper
import net.explorviz.server.security.AuthenticationEndpoint
import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.server.ResourceConfig
import javax.ws.rs.ApplicationPath

@ApplicationPath("")
class ExplorViz extends ResourceConfig {
	new() {
		register(new DependencyInjectionBinder())
		register(JacksonFeature)
		
		// Authentication & Authorization
		register(AuthenticationEndpoint)
		packages("net.explorviz.server.filters")
		
		// resources
		packages("net.explorviz.resources")
		
		// exception handling (mind the order !)
		register(QueryParamExceptionMapper)
		register(PathParamExceptionMapper)
		register(GeneralExceptionMapper)
		
		// register plugins (For development: read plugin structure at github docs)
		packages("net.explorviz.plugins")
	}
}
