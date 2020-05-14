package net.explorviz.broadcast.security;

import java.security.Principal;
import java.util.List;

/**
 * Model for details of an authenticated user.
 */
public final class AuthenticatedUserDetails implements Principal {

  private final String username;
  private final List<String> roles;

  public AuthenticatedUserDetails(final String username, final List<String> roles) {
    this.username = username;
    this.roles = roles;
  }

  public List<String> getRoles() {
    return this.roles;
  }

  @Override
  public String getName() {
    return this.username;
  }
}
