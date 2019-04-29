package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Type;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Type("doublesetting")
public class DoubleSetting extends Setting<Double> {

  @JsonProperty("min")
  private final Double min;

  @JsonProperty("max")
  private final Double max;

  /**
   * {@inheritDoc}
   */
  public DoubleSetting(final String id, final String name, final String description,
      final Double defaultValue, final String origin) {
    this(id, name, description, defaultValue, origin, Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY);
  }

  public DoubleSetting() {
    super();
    this.min = Double.NEGATIVE_INFINITY;
    this.max = Double.POSITIVE_INFINITY;
  }

  /**
   *
   * {@inheritDoc}
   *
   * @param minValue the minimal acceptable value for this setting
   * @param maxValue the maximal acceptable value for this setting
   */
  public DoubleSetting(final String id, final String name, final String description,
      final Double defaultValue, final String origin, final double minValue,
      final double maxValue) {
    super(id, name, description, defaultValue, origin);
    this.min = minValue;
    this.max = maxValue;
  }

  /**
   * Returns the minimal acceptable value.
   *
   * @return the minimal acceptable value (default is {@code Double.NEGATIVE_INFINITY})
   */
  public double getMinValue() {
    return this.min;
  }

  /**
   * Returns the maximal acceptable value.
   *
   * @return the maximal acceptable value (default is {@code Double.POSITIVE_INFINITY})
   */
  public double getMaxValue() {
    return this.max;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append(this.getId()).append(this.getName())
        .append(this.getDescription()).append("default", this.getDefaultValue())
        .append("min", this.getMinValue()).append("max", this.getMaxValue()).build();
  }

}
