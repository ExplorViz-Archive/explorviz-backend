package net.explorviz.settings.services;

/**
 * Exception that a setting, that was searched for, does not exist.
 *
 */
@SuppressWarnings("serial")
public class UnknownSettingException extends Exception {

  public UnknownSettingException() {
    super();
  }

}
