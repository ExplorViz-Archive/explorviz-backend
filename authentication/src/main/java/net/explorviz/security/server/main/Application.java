package net.explorviz.security.server.main;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import net.explorviz.security.server.filter.CORSResponseFilter;

@ApplicationPath("")
public class Application extends ResourceConfig {

	public Application() {

		// register DI
		register(new DependencyInjectionBinder());

		// Enable CORS
		register(CORSResponseFilter.class);

		// Authentication Logic
		// register(AuthenticationFilter.class);

		// Authorization Logic
		// register(AuthorizationFilter.class);

		// register all providers in the given package
		packages("net.explorviz.security.server.providers");

		// register all resources in the given package
		packages("net.explorviz.security.server.resources");
	}
}
