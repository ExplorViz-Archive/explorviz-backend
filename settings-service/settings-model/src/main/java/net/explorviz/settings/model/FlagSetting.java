package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Type;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents a flag that can either be set (value = true) or not (value = false).
 *
 */
@Type("flagsetting")
public class FlagSetting extends Setting {

  private boolean defaultValue;

  /**
   * Creates a new flag.
   * 
   * @param id the id
   * @param displayName the display name
   * @param description a brief description
   * @param origin the origin
   * @param defaultValue the default value
   */
  @JsonCreator
  public FlagSetting(@JsonProperty("id") final String id,
      @JsonProperty("displayName") final String displayName,
      @JsonProperty("description") final String description,
      @JsonProperty("origin") final String origin,
      @JsonProperty("defaultValue") final boolean defaultValue) {
    super(id, displayName, description, origin);
    this.defaultValue = defaultValue;
  }

  /**
   * Creates a new flag.
   * 
   * @param displayName the display name
   * @param description a brief description
   * @param origin the origin
   * @param defaultValue the default value
   */
  public FlagSetting(final String displayName, final String description, final String origin,
      final boolean defaultValue) {
    super(displayName, description, origin);
    this.defaultValue = defaultValue;
  }

  @SuppressWarnings("unused")
  private FlagSetting() {
    // Morphia
  }

  public boolean getDefaultValue() {
    return this.defaultValue;
  }



  public void setDefaultValue(final boolean defaultValue) {
    this.defaultValue = defaultValue;
  }


  @Override
  public String toString() {
    return new ToStringBuilder(this).append("default", this.defaultValue)
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

    final FlagSetting that = (FlagSetting) o;

    return new EqualsBuilder().appendSuper(super.equals(o))
        .append(this.defaultValue, that.defaultValue)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode())
        .append(this.defaultValue)
        .toHashCode();
  }
}
