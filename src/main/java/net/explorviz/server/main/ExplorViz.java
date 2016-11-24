package net.explorviz.server.main;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import net.explorviz.resources.LandscapeResource;
import net.explorviz.server.filters.CORSResponseFilter;
import net.explorviz.server.security.AuthenticationEndpoint;

public class ExplorViz extends ResourceConfig {
	public ExplorViz() {

		register(new DependencyInjectionBinder());
		register(JacksonFeature.class);
		
		// Authentication & Authorization
		register(AuthenticationEndpoint.class);
		register(CORSResponseFilter.class);
		
		// resources
		register(LandscapeResource.class);
		
		// exception handling
		register(GeneralExceptionMapper.class);
	}
}
