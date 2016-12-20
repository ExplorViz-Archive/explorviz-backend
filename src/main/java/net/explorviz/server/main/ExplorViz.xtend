package net.explorviz.server.main

import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.server.ResourceConfig
import net.explorviz.server.security.AuthenticationEndpoint

class ExplorViz extends ResourceConfig {
	new() {
		register(new DependencyInjectionBinder())
		register(JacksonFeature)
		// Authentication & Authorization
		register(AuthenticationEndpoint)
		packages("net.explorviz.server.filters")
		// resources
		packages("net.explorviz.resources")
		// exception handling
		register(GeneralExceptionMapper)
	}
}
