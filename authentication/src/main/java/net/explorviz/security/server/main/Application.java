package net.explorviz.security.server.main;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import net.explorviz.security.server.filter.AuthenticationFilter;
import net.explorviz.security.server.filter.CORSResponseFilter;

@ApplicationPath("")
public class Application extends ResourceConfig {

	public Application() {

		// register CDI
		register(new DependencyInjectionBinder());

		register(AuthenticationFilter.class);
		register(CORSResponseFilter.class);

		// register all resources in the given package
		packages("net.explorviz.security.server.resources");
	}
}
