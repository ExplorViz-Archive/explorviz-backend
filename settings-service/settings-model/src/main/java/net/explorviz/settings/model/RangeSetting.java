package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Type;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
   * 
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

  /**
   * Creates a new range setting.
   * 
   * @param displayName the display name
   * @param description a brief description of the settings effects
   * @param origin the origin of the setting
   * @param defaultValue its default value
   * @param min the lower bound (inclusively)
   * @param max the upper bound (inclusively)
   */
  public RangeSetting(final String displayName, final String description, final String origin,
      final double defaultValue, final double min, @JsonProperty("max") final double max) {
    super(displayName, description, origin);
    this.defaultValue = defaultValue;
    this.min = min;
    this.max = max;
  }

  @SuppressWarnings("unused")
  private RangeSetting() {
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
    return new ToStringBuilder(this).append("defautl", this.defaultValue)
        .append("min", this.min)
        .append("max", this.max)
        .appendSuper(super.toString())
        .build();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final RangeSetting that = (RangeSetting) o;

    return new EqualsBuilder().appendSuper(super.equals(o))
        .append(this.defaultValue, that.defaultValue)
        .append(this.min, that.min)
        .append(this.max, that.max)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode())
        .append(this.defaultValue)
        .append(this.min)
        .append(this.max)
        .toHashCode();
  }
}
