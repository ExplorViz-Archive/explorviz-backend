package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Type;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Type("doublesetting")
public class DoubleSetting extends Setting<Double>{

  @JsonProperty("min")
  private Double min;
  
  @JsonProperty("max")
  private Double max;
  
  /**
   * {@inheritDoc}
   */
  public DoubleSetting(String id, String name, String description, Double defaultValue, String origin) {
    this(id, name, description, defaultValue, origin, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
  }
  
  public DoubleSetting() {
   super();
   min = Double.NEGATIVE_INFINITY;
   max = Double.POSITIVE_INFINITY;
  }
  
  /**
   * 
   * {@inheritDoc}
   * @param minValue the minimal acceptable value for this setting
   * @param maxValue the maximal acceptable value for this setting
   */
  public DoubleSetting(String id, String name, String description, Double defaultValue, String origin, double minValue, double maxValue) {
    super(id, name, description, defaultValue, origin);
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
  
  @Override
  public String toString() {
    return new ToStringBuilder(this).append(this.getId())
        .append(this.getName())
        .append(this.getDescription())
        .append("default", this.getDefaultValue())
        .append("min", getMinValue())
        .append("max", getMaxValue())
        .build();
  }

}
