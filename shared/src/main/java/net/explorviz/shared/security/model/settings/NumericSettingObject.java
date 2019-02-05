package net.explorviz.shared.security.model.settings;

public class NumericSettingObject extends SettingObject<Number> {


  private Number min = Long.MIN_VALUE;

  private Number max = Long.MAX_VALUE;



  public NumericSettingObject(final String name, final Number value, final String description) {
    super(name, value, description);
  }


  public NumericSettingObject(final String name, final Number value, final String description,
      final Number min, final Number max) {
    this(name, value, description);
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
