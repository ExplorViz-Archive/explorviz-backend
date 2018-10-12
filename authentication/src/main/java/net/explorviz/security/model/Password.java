package net.explorviz.security.model;

/**
 * Model for a password. Solely needed for parsing when changing passwords.
 */
public class Password {

  private String password;



  public Password(final String password) {
    super();
    this.password = password;
  }



  public Password() {
    super();
  }



  public String getPassword() {
    return this.password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }



}
