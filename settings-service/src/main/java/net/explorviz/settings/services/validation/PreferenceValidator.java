package net.explorviz.settings.services.validation;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserPreference;

/**
 * Class to be used for validation of {@link UserPreference} according to a {@link Setting} and its
 * constraints.
 *
 */
public class PreferenceValidator {


  private final UserPreference up;

  private final List<Class<?>> settingTypes = new ArrayList<>();



  /**
   * Creates a new validator specific for the given {@link UserPreference}. The validator works by
   * checking all constraints defined in the given {@link FlagSetting} object hold regarding the
   * given user preference.
   *
   * @param userPreference the user preference object
   */
  public PreferenceValidator(final UserPreference userPreference) {
    super();
    this.settingTypes.add(FlagSetting.class);
    this.settingTypes.add(RangeSetting.class);
    this.up = userPreference;

  }

  /**
   * Validates user preference according to the given setting. This method terminates without
   * throwing an exception iff the validation the user preferences are valid according to the
   * settings object.
   *
   * @param setting the settings to validate against
   * @throws PreferenceValidationException thrown if the user preferences are invalid.
   */
  public void validate(final Setting setting) throws PreferenceValidationException {

    if (setting instanceof FlagSetting) {
      this.validate((FlagSetting) setting);
    } else if (setting instanceof RangeSetting) {
      this.validate((RangeSetting) setting);
    } else {
      throw new IllegalStateException("There is no validator for the given type");
    }
  }



  /**
   * If the preference is valid, the method will terminate without throwing an exception.
   *
   * @param setting the setting to validate against
   * @throws PreferenceValidationException thrown if the preference is invalid according to the
   *         setting object. The message of the exception contains the reason.
   */
  private void validate(final FlagSetting setting) throws PreferenceValidationException {
    try {
      @SuppressWarnings("unused")
      final boolean val = (boolean) this.up.getValue();
    } catch (final ClassCastException e) {
      throw new PreferenceValidationException("Given value is not a boolean");
    }

  }

  /**
   * If the preference is valid, the method will terminate without throwing an exception.
   *
   * @param setting the setting to validate against
   * @throws PreferenceValidationException thrown if the preference is invalid according to the
   *         setting object. The message of the exception contains the reason.
   */
  private void validate(final RangeSetting setting) throws PreferenceValidationException {

    // check if types match
    Number num;
    final Double val;
    try {
      num = (Number) this.up.getValue();
      val = num.doubleValue();
    } catch (final ClassCastException e) {
      throw new PreferenceValidationException(
          String.format("Given value is not of type double but %s",
              this.up.getValue().getClass().toString()));
    }

    // Types match, check if value lies within min an max

    if (val < setting.getMin() || val > setting.getMax()) {
      throw new PreferenceValidationException(String
          .format("Value must be in the interval [%f, %f]", setting.getMin(), setting.getMax()));
    }

  }



}
