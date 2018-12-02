package net.explorviz.shared.security;

import java.security.Principal;
import java.util.List;
import net.explorviz.shared.security.model.roles.Role;

/**
 * Model for details of an authenticated user.
 */
public final class AuthenticatedUserDetails implements Principal {

  private final String username;
  private final List<Role> roles;

  public AuthenticatedUserDetails(final String username, final List<Role> roles) {
    this.username = username;
    this.roles = roles;
  }

  public List<Role> getRoles() {
    return this.roles;
  }

  @Override
  public String getName() {
    return this.username;
  }
}
