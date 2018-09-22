package net.explorviz.security.model;

/**
 * Model for user credentials, i.e., password and username.
 */
public class UserCredentials {

  private String username;
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }
}
