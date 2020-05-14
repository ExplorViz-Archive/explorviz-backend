package net.explorviz.broadcast.security.filters;

import io.quarkus.security.UnauthorizedException;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import net.explorviz.broadcast.security.TokenParser;


/**
 * Custom {@link ContainerRequestFilter} that is used for JWT-based authentication.
 *
 * <p>
 * The AuthenticationFilter extracts the JWT details from the HTTP Authorization header (Bearer)
 * and verifies its validity. If the token is invalid, the request is aborted
 * </p>
 */
@Provider
public class ValidJWTFilter implements ContainerRequestFilter {

  // Credit: https://github.com/cassiomolin/jersey-jwt

  @Inject
  TokenParser tokenParser;

  public ValidJWTFilter() {
  }


  @Override
  public void filter(final ContainerRequestContext requestContext) {

    final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      final String authenticationToken = authorizationHeader.substring(7);

      tokenParser.verifyToken(authenticationToken);
    } else {
      throw new UnauthorizedException();
    }
  }

}
