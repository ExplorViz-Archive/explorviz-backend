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

import net.explorviz.security.services.TokenService;
import net.explorviz.shared.annotations.Secured;
import net.explorviz.shared.security.AuthenticatedUserDetails;
import net.explorviz.shared.security.TokenBasedSecurityContext;
import net.explorviz.shared.security.TokenDetails;
import net.explorviz.shared.security.User;

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

		if (method.getName().equals("apply")) {
			System.out.println(method.getDeclaringClass());
			// TODO where does the apply message come from?
			// It is only called, if the request is not issued with curl but the frontend
			return;
		}

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

		// non-annotated classes cannot be accessed
		throw new ForbiddenException("Could not be authenticated");

	}

	private void handleTokenBasedAuthentication(final String authenticationToken,
			final ContainerRequestContext requestContext) {

		// prepare securityContext for usage in resource

		final TokenDetails tokenDetails = this.tokenService.parseToken(authenticationToken);

		// TODO find user in DB
		if (tokenDetails.getUsername().equals("admin")) {

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