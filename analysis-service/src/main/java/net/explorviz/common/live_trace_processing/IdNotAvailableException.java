package net.explorviz.common.live_trace_processing;

public class IdNotAvailableException extends Exception {

  public IdNotAvailableException(final Integer id) {
    super(id.toString());
  }

  private static final long serialVersionUID = -1996369560534377771L;

}
