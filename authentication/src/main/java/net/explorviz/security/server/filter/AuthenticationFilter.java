package net.explorviz.security.server.filter;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.annotation.Priority;
import javax.annotation.security.PermitAll;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import net.explorviz.shared.annotations.Secured;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		final Method method = resourceInfo.getResourceMethod();

		if (method.isAnnotationPresent(PermitAll.class)) {
			// nothing to do
			return;
		}

		if (method.isAnnotationPresent(Secured.class)) {
			final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				final String authenticationToken = authorizationHeader.substring(7);
				handleTokenBasedAuthentication(authenticationToken, requestContext);
				return;
			}
		}

	}

	private void handleTokenBasedAuthentication(final String authenticationToken,
			final ContainerRequestContext requestContext) {

		if (authenticationToken.length() > 5) {
			// TODO check if Token is valid
			System.out.println("Test");
		} else {
			throw new ForbiddenException("Not allowed.");
		}

	}
}