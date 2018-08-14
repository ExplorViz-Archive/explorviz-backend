package net.explorviz.shared.security.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import net.explorviz.shared.security.AuthenticatedUserDetails;
import net.explorviz.shared.security.TokenBasedSecurityContext;
import net.explorviz.shared.security.TokenDetails;
import net.explorviz.shared.security.TokenParserService;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	// Credit: https://github.com/cassiomolin/jersey-jwt

	@Inject
	private TokenParserService tokenParser;

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

		final TokenDetails tokenDetails = this.tokenParser.parseToken(authenticationToken);

		final AuthenticatedUserDetails authenticatedUserDetails = new AuthenticatedUserDetails(
				tokenDetails.getUsername(), tokenDetails.getRoles());

		final boolean isSecure = requestContext.getSecurityContext().isSecure();
		final SecurityContext securityContext = new TokenBasedSecurityContext(authenticatedUserDetails, tokenDetails,
				isSecure);

		requestContext.setSecurityContext(securityContext);
	}
}