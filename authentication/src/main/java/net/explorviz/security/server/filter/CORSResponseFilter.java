package net.explorviz.security.server.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * ResponseFilter for CORS support. Frontend and Backend are on different
 * origins, since Same Origin Policy defines origins to be equal when protocol,
 * host and port is the same.
 *
 */
public class CORSResponseFilter implements ContainerResponseFilter {

	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
			throws IOException {

		final MultivaluedMap<String, Object> headers = responseContext.getHeaders();

		if (!headers.containsKey("Access-Control-Allow-Origin")) {
			headers.add("Access-Control-Allow-Origin", "*");
			headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
			headers.add("Access-Control-Allow-Headers",
					"Origin, X-Requested-With, Content-Type, Accept, Authorization");
		}

	}

}
