package net.explorviz.security.server.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

/**
 * JWT authentication filter.
 *
 * https://github.com/cassiomolin/jersey-jwt
 *
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			final String authenticationToken = authorizationHeader.substring(7);
			handleTokenBasedAuthentication(authenticationToken, requestContext);
			return;
		}
	}

	private void handleTokenBasedAuthentication(final String authenticationToken,
			final ContainerRequestContext requestContext) {

		if (authenticationToken.length() > 5) {
			System.out.println("All Good");
		} else {
			throw new ForbiddenException("Not allowed.");
		}

	}
}