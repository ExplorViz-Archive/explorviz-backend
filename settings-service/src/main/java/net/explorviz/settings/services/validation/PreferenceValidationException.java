package net.explorviz.settings.services.validation;

import net.explorviz.settings.model.UserPreference;

/**
 * Exception that indicates that the validation of a {@link UserPreference} has failed.
 *
 */
@SuppressWarnings("serial")
public class PreferenceValidationException extends Exception {

  public PreferenceValidationException() {

  }

  public PreferenceValidationException(final String arg0) {
    super(arg0);
  }

  public PreferenceValidationException(final Throwable arg0) {
    super(arg0);
  }

  public PreferenceValidationException(final String arg0, final Throwable arg1) {
    super(arg0, arg1);
  }

  public PreferenceValidationException(final String arg0, final Throwable arg1, final boolean arg2,
      final boolean arg3) {
    super(arg0, arg1, arg2, arg3);
  }

}
