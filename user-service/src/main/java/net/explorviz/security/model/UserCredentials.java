package net.explorviz.security.model;

/**
 * Model for user credentials, i.e., password and username.
 */
public class UserCredentials {

  private String username;
  private String password;

  public UserCredentials(final String username, final String password) {
    this.username = username;
    this.password = password;
  }

  public UserCredentials() {}

  public String getUsername() {
    return this.username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }
}
