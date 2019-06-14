package net.explorviz.security.services.exceptions;

/**
 * Sub-Exception of {@link UserCrudException} that is thrown when a user is tried to persist, that
 * already exists.
 *
 */
@SuppressWarnings("serial")
public class DuplicateUserException extends UserCrudException {

  public DuplicateUserException() {
    super();
    // TODO Auto-generated constructor stub
  }

  public DuplicateUserException(final String msg, final Throwable cause) {
    super(msg, cause);
    // TODO Auto-generated constructor stub
  }

  public DuplicateUserException(final String msg) {
    super(msg);
    // TODO Auto-generated constructor stub
  }



}
