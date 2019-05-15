package net.explorviz.security.model;

/**
 * Model for a JSON Web Token.
 *
 *
 */
public class Token {

  private String stringifiedToken;

  public String getToken() {
    return this.stringifiedToken;
  }

  public void setToken(final String token) {
    this.stringifiedToken = token;
  }
}
