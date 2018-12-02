package net.explorviz.shared.security;

import java.security.Principal;
import javax.ws.rs.core.SecurityContext;
import net.explorviz.shared.security.filters.AuthenticationFilter;
import net.explorviz.shared.security.filters.AuthorizationFilter;
import net.explorviz.shared.security.model.TokenDetails;
import net.explorviz.shared.security.model.roles.Role;

/**
 * Custom {@link SecurityContext} that holds security details for the authorization process.
 * See @{@link AuthenticationFilter} and {@link AuthorizationFilter} for examples.
 */
public class TokenBasedSecurityContext implements SecurityContext {

  private final AuthenticatedUserDetails authenticatedUserDetails;
  private final TokenDetails authenticationTokenDetails;
  private final boolean secure;

  /**
   * Constructor for this custom {@link SecurityContext}.
   *
   * @param authenticatedUserDetails - Contains details of an authenticated user, e.g., the roles
   * @param authenticationTokenDetails - Java model of the JSON web token for this user
   * @param secure - A boolean indicating whether this request was made using a secure channel, such
   *        as HTTPS.
   */
  public TokenBasedSecurityContext(final AuthenticatedUserDetails authenticatedUserDetails,
      final TokenDetails authenticationTokenDetails, final boolean secure) {
    this.authenticatedUserDetails = authenticatedUserDetails;
    this.authenticationTokenDetails = authenticationTokenDetails;
    this.secure = secure;
  }

  @Override
  public Principal getUserPrincipal() {
    return this.authenticatedUserDetails;
  }

  @Override
  public boolean isUserInRole(final String s) {
    for (final Role r : this.authenticatedUserDetails.getRoles()) {
      if (r.getDescriptor().equals(s)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isSecure() {
    return this.secure;
  }

  @Override
  public String getAuthenticationScheme() {
    return "Bearer";
  }

  public TokenDetails getTokenDetails() {
    return this.authenticationTokenDetails;
  }
}
