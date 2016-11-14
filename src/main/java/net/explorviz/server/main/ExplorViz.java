package net.explorviz.server.main;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import net.explorviz.resources.LandscapeResource;

public class ExplorViz extends ResourceConfig {
	public ExplorViz() {

		register(new DependencyInjectionBinder());
		register(JacksonFeature.class);
		register(LandscapeResource.class);
		register(GeneralExceptionMapper.class);
		register(RolesAllowedDynamicFeature.class);

	}
}
