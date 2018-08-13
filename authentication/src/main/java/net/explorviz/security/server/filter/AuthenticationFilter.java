package net.explorviz.security.server.filter;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.annotation.Priority;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import net.explorviz.security.model.AuthenticatedUserDetails;
import net.explorviz.security.model.TokenDetails;
import net.explorviz.security.model.User;
import net.explorviz.security.server.main.TokenBasedSecurityContext;
import net.explorviz.security.services.TokenService;
import net.explorviz.shared.annotations.Secured;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	// @Inject
	// private UserService userService;

	@Inject
	private TokenService tokenService;

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

		throw new ForbiddenException("Could not be authenticated");

	}

	private void handleTokenBasedAuthentication(final String authenticationToken,
			final ContainerRequestContext requestContext) {

		// prepare securityContext for usage in resource

		final TokenDetails tokenDetails = this.tokenService.parseToken(authenticationToken);

		System.out.println("hii");

		// TODO find user in DB
		if (tokenDetails.getUsername().equals("admin")) {

			System.out.println("hii");
			final User user = new User(tokenDetails.getUsername());

			final AuthenticatedUserDetails authenticatedUserDetails = new AuthenticatedUserDetails(user.getUsername(),
					user.getRoles());

			final boolean isSecure = requestContext.getSecurityContext().isSecure();
			final SecurityContext securityContext = new TokenBasedSecurityContext(authenticatedUserDetails,
					tokenDetails, isSecure);

			requestContext.setSecurityContext(securityContext);

			return;
		}

		throw new ForbiddenException("Could not be authenticated");
	}
}