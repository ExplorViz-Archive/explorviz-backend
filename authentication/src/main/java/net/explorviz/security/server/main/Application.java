package net.explorviz.security.server.main;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import net.explorviz.security.server.filter.AuthenticationFilter;
import net.explorviz.security.server.filter.CORSResponseFilter;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.shared.exceptions.mapper.WebApplicationExceptionMapper;

@ApplicationPath("")
public class Application extends ResourceConfig {

	public Application() {

		// register CDI
		register(new DependencyInjectionBinder());

		register(AuthenticationFilter.class);
		register(CORSResponseFilter.class);

		// exception handling (mind the order !)
		register(WebApplicationExceptionMapper.class);
		register(GeneralExceptionMapper.class);

		// register all resources in the given package
		packages("net.explorviz.security.server.resources");
	}
}
