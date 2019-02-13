package net.explorviz.shared.security.model.settings;

import com.github.jasminb.jsonapi.annotations.Type;

@Type("numericsettingsdescriptor")
public class NumericSettingDescriptor extends SettingDescriptor<Double> {


  private double min = Long.MIN_VALUE;

  private double max = Long.MAX_VALUE;



  public NumericSettingDescriptor(final String id, final String name, final String description,
      final Double defaultValue) {
    super(id, name, description, defaultValue);
  }


  /**
   * Creates a new descriptor for a numeric (double) setting.
   *
   * @param id the id, which should resemble the name of the setting
   * @param name the name of the setting
   * @param description short description
   * @param min the minimum value (inclusive), default is {@code Long.MIN_VALUE}
   * @param max the maximum value (inclusive), default is {@code Long.MAX_VALUE}
   * @param defaultValue
   */
  public NumericSettingDescriptor(final String id, final String name, final String description,
      final double min, final double max, final Double defaultValue) {
    this(id, name, description, defaultValue);
    this.min = min;
    this.max = max;
  }


  public double getMin() {
    return this.min;
  }

  public double getMax() {
    return this.max;
  }

  public boolean isInRange() {
    // TODO: Number is not good for comparison since we can't know if the value is integral. Use
    // double instead?
    return false;
  }

}
