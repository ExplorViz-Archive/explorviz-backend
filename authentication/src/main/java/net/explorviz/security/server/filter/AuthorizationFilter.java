package net.explorviz.security.server.filter;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		System.out.println("TOTOTOTOTOTOTOTOTO");

		final Method method = resourceInfo.getResourceMethod();

		// @DenyAll on the method takes precedence over @RolesAllowed and @PermitAll
		if (method.isAnnotationPresent(DenyAll.class)) {
			throw new ForbiddenException("You don't have permissions to perform this action.");
		}

		// @RolesAllowed on the method takes precedence over @PermitAll
		RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
		if (rolesAllowed != null) {
			performAuthorization(rolesAllowed.value(), requestContext);
			return;
		}

		// @PermitAll on the method takes precedence over @RolesAllowed on the class
		if (method.isAnnotationPresent(PermitAll.class)) {
			// Do nothing
			return;
		}

		// @DenyAll can't be attached to classes

		// @RolesAllowed on the class takes precedence over @PermitAll on the class
		rolesAllowed = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
		if (rolesAllowed != null) {
			performAuthorization(rolesAllowed.value(), requestContext);
		}

		// @PermitAll on the class
		if (resourceInfo.getResourceClass().isAnnotationPresent(PermitAll.class)) {
			// Do nothing
			return;
		}

		// Authentication is required for non-annotated methods
		if (!isAuthenticated(requestContext)) {
			throw new ForbiddenException("Authentication is required to perform this action.");
		}
	}

	/**
	 * Perform authorization based on roles.
	 *
	 * @param rolesAllowed
	 * @param requestContext
	 */
	private void performAuthorization(final String[] rolesAllowed, final ContainerRequestContext requestContext) {

		if (rolesAllowed.length > 0 && !isAuthenticated(requestContext)) {
			throw new ForbiddenException("Authentication is required to perform this action.");
		}

		for (final String role : rolesAllowed) {
			if (requestContext.getSecurityContext().isUserInRole(role)) {
				return;
			}
		}

		throw new ForbiddenException("You don't have permissions to perform this action.");
	}

	/**
	 * Check if the user is authenticated.
	 *
	 * @param requestContext
	 * @return
	 */
	private boolean isAuthenticated(final ContainerRequestContext requestContext) {
		return requestContext.getSecurityContext().getUserPrincipal() != null;
	}
}