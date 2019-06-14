package net.explorviz.security.services.exceptions;

import net.explorviz.security.model.UserBatchRequest;

/**
 * Denotes that the given {@link UserBatchRequest} can not be fulfilled.
 */
@SuppressWarnings("serial")
public class MalformedBatchRequestException extends UserCrudException {

  public MalformedBatchRequestException() {
    super();
    // TODO Auto-generated constructor stub
  }

  public MalformedBatchRequestException(final String msg, final Throwable cause) {
    super(msg, cause);
    // TODO Auto-generated constructor stub
  }

  public MalformedBatchRequestException(final String msg) {
    super(msg);
    // TODO Auto-generated constructor stub
  }



}
