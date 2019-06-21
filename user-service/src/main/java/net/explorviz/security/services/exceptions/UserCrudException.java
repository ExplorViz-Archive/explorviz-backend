package net.explorviz.security.services.exceptions;

/**
 * Exception for all errors that occur during user CRUD operations.
 */
@SuppressWarnings("serial")
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
