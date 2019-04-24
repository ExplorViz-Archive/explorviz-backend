package net.explorviz.security.services;

/**
 * Exception for all errors that occur during user CRUD operations.
 */
public class UserCrudException extends Exception {

  public UserCrudException() {
    super();
  }

  public UserCrudException(final String msg) {
    super(msg);
  }


  public UserCrudException(final String msg, final Throwable cause) {
    super(msg, cause);
  }

}