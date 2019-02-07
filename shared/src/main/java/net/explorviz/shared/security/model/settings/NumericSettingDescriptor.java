package net.explorviz.shared.security.model.settings;

public class NumericSettingDescriptor extends SettingDescriptor<Double> {


  private Number min = Long.MIN_VALUE;

  private Number max = Long.MAX_VALUE;



  public NumericSettingDescriptor(final String name, final String description,
      final Double defaultValue) {
    super(name, description, defaultValue);
  }


  public NumericSettingDescriptor(final String name, final String description, final Number min,
      final Number max, final Double defaultValue) {
    this(name, description, defaultValue);
    this.min = min;
    this.max = max;
  }


  public Number getMin() {
    return this.min;
  }

  public Number getMax() {
    return this.max;
  }

  public boolean isInRange() {
    // TODO: Number is not good for comparison since we can't know if the value is integral. Use
    // double instead?
    return false;
  }

}
