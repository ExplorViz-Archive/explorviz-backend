package net.explorviz.settings.services.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.UserPreference;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link PreferenceValidator}.
 *
 */
public class PreferenceValidatorTest {

  private static final String FLAGID = "f";
  private static final String RANGEID = "r";
  private static final String NAME = "name";
  private static final String DESC = "dec";
  private static final String ORIGIN = "origin";


  private static final String ERROR = "Valid preference but exception thrown";


  private static final double MIN = -10.01;
  private static final double MAX = 102;

  private final FlagSetting flag = new FlagSetting(FLAGID, NAME, DESC, ORIGIN, false);
  private final RangeSetting range = new RangeSetting(RANGEID, NAME, DESC, ORIGIN, 1, 0, 2);

  private PreferenceValidator validator;



  @Test
  public void testFlagValid() {
    final UserPreference up = new UserPreference("id", "1", NAME, true);
    this.validator = new PreferenceValidator(up);
    try {
      this.validator.validate(this.flag);
    } catch (final PreferenceValidationException e) {
      fail(ERROR);
    }

  }

  @Test
  public void testFlagInvalidType() {
    final UserPreference up = new UserPreference("id", "1", NAME, 0.5);
    this.validator = new PreferenceValidator(up);
    assertThrows(PreferenceValidationException.class, () -> this.validator.validate(this.flag));

  }

  @Test
  public void testRangeValid() {
    final UserPreference up = new UserPreference("id", "1", NAME, 1);
    this.validator = new PreferenceValidator(up);
    try {
      this.validator.validate(this.range);
    } catch (final PreferenceValidationException e) {
      e.printStackTrace();
      fail(ERROR);
    }

  }

  @Test
  public void testRangeLessThanMin() {
    final UserPreference up = new UserPreference("id", "1", NAME, MIN - 0.1);
    this.validator = new PreferenceValidator(up);
    assertThrows(PreferenceValidationException.class, () -> this.validator.validate(this.range));
  }

  @Test
  public void testRangeGreatherThanMax() {
    final UserPreference up = new UserPreference("id", "1", NAME, MAX + 0.1);
    this.validator = new PreferenceValidator(up);
    assertThrows(PreferenceValidationException.class, () -> this.validator.validate(this.range));
  }

  @Test
  public void testRangeInvalidType() {
    final UserPreference up = new UserPreference("id", "1", NAME, "notadouble");
    this.validator = new PreferenceValidator(up);
    assertThrows(PreferenceValidationException.class, () -> this.validator.validate(this.range));
  }

}
