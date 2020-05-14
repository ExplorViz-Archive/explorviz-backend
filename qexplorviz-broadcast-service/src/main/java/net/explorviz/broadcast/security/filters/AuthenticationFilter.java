package net.explorviz.broadcast.security.filters;

import java.io.IOException;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import net.explorviz.broadcast.security.AuthenticatedUserDetails;
import net.explorviz.broadcast.security.TokenBasedSecurityContext;
import net.explorviz.broadcast.security.TokenDetails;
import net.explorviz.broadcast.security.TokenParser;


/**
 * Custom {@link ContainerRequestFilter} that is used for JWT-based authentication. If used in a web
 * service, every request will be first processed by this filter and afterwards by the related
 * {@link AuthorizationFilter}.
 *
 * <p>
 * The AuthenticationFilter extracts the JWT details from the HTTP Authorization header (Bearer).
 * Additionally, it prepares the {@link TokenBasedSecurityContext} for the AuthorizationFilter.
 * Authentication error exceptions are thrown in the {@link AuthorizationFilter}.
 * </p>
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@Secure
public class AuthenticationFilter implements ContainerRequestFilter {

  // Credit: https://github.com/cassiomolin/jersey-jwt

  @Inject
  TokenParser tokenParser;


  public AuthenticationFilter(TokenParser tokenParser) {
    this.tokenParser = tokenParser;
  }

  public AuthenticationFilter() {}

  @Override
  public void filter(final ContainerRequestContext requestContext) {
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
    final AuthenticatedUserDetails authenticatedUserDetails =
        new AuthenticatedUserDetails(tokenDetails.getUsername(), tokenDetails.getRoles());

    final boolean isSecure = requestContext.getSecurityContext().isSecure();
    final SecurityContext securityContext =
        new TokenBasedSecurityContext(authenticatedUserDetails, tokenDetails, isSecure);
    requestContext.setSecurityContext(securityContext);
  }
}
