package net.explorviz.server.main;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import net.explorviz.resources.LandscapeResource;
import net.explorviz.server.AuthenticationEndpoint;

public class ExplorViz extends ResourceConfig {
	public ExplorViz() {

		register(new DependencyInjectionBinder());
		register(JacksonFeature.class);
		register(LandscapeResource.class);
		register(AuthenticationEndpoint.class);
		register(GeneralExceptionMapper.class);
	}
}
