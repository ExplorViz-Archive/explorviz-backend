package net.explorviz.shared.security.filters;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * ResponseFilter for CORS support.
 */
@Provider
public class CorsResponseFilter implements ContainerResponseFilter {

  @Override
  public void filter(final ContainerRequestContext requestContext,
      final ContainerResponseContext responseContext) throws IOException {

    final MultivaluedMap<String, Object> headers = responseContext.getHeaders();

    headers.add("Access-Control-Allow-Origin", "*");
    headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH");
    headers.add("Access-Control-Allow-Headers",
        "Origin, X-Requested-With, Content-Type, Accept, Authorization");
  }

}
