package net.explorviz.settings.services.validation;

import net.explorviz.settings.model.UserPreference;

/**
 * Exception that indicates that the validation of a {@link UserPreference} has failed.
 */
@SuppressWarnings("serial")
public class PreferenceValidationException extends Exception {

  public PreferenceValidationException(final String arg0) {
    super(arg0);
  }

  public PreferenceValidationException(final String s, final Throwable cause) {
    super(s, cause);
  }
}
