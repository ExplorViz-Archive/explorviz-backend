package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Type;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Represents a value that lies between an upper and lower bound. 
 *
 */
@Type("rangesetting")
public class RangeSetting extends Setting {

  private double defaultValue;

  private double min;

  private double max;

  /**
   * Creates a new range setting.
   * @param id the id
   * @param displayName the display name
   * @param description a brief description of the settings effects
   * @param origin the origin of the setting
   * @param defaultValue its default value
   * @param min the lower bound (inclusively)
   * @param max the upper bound (inclusively)
   */
  @JsonCreator
  public RangeSetting(@JsonProperty("id") final String id,
      @JsonProperty("displayName") final String displayName,
      @JsonProperty("description") final String description,
      @JsonProperty("origin") final String origin,
      @JsonProperty("defaultValue") final double defaultValue,
      @JsonProperty("min") final double min, @JsonProperty("max") final double max) {
    super(id, displayName, description, origin);
    this.defaultValue = defaultValue;
    this.min = min;
    this.max = max;
  }

  public RangeSetting() {
    // Morphia
  }



  public double getDefaultValue() {
    return this.defaultValue;
  }

  public double getMax() {
    return this.max;
  }

  public double getMin() {
    return this.min;
  }

  public void setDefaultValue(final double defaultValue) {
    this.defaultValue = defaultValue;
  }

  public void setMax(final double max) {
    this.max = max;
  }

  public void setMin(final double min) {
    this.min = min;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("defautl", this.defaultValue).append("min", this.min)
        .append("max", this.max).appendSuper(super.toString()).build();
  }

}