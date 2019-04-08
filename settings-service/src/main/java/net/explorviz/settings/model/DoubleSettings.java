package net.explorviz.settings.model;

public class DoubleSettings extends Setting<Double>{

  private final double min, max;
  
  /**
   * {@inheritDoc}
   */
  public DoubleSettings(String id, String name, String description, Double defaultValue) {
    this(id, name, description, defaultValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
  }
  
  /**
   * 
   * {@inheritDoc}
   * @param minValue the minimal acceptable value for this setting
   * @param maxValue the maximal acceptable value for this setting
   */
  public DoubleSettings(String id, String name, String description, Double defaultValue, double minValue, double maxValue) {
    super(id, name, description, defaultValue);
    min = minValue;
    max = maxValue;
  }
  
  /**
   * Returns the minimal acceptable value.
   * @return the minimal acceptable value (default is {@code Double.NEGATIVE_INFINITY})
   */
  public double getMinValue() {
    return min;
  }
  
  /**
   * Returns the maximal acceptable value.
   * @return the maximal acceptable value (default is {@code Double.POSITIVE_INFINITY})
   */
  public double getMaxValue() {
    return max;
  }

}
