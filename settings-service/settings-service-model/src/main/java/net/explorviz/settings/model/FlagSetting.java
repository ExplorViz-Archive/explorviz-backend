package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Type;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents a flag that can either be set (value = true) or not (value = false).
 *
 */
@Type("flagsetting")
public class FlagSetting extends Setting {

  private boolean defaultValue;

  @JsonCreator
  public FlagSetting(@JsonProperty("id") final String id,
      @JsonProperty("displayName") final String displayName,
      @JsonProperty("description") final String description,
      @JsonProperty("origin") final String origin,
      @JsonProperty("defaultValue") final boolean defaultValue) {
    super(id, displayName, description, origin);
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
        .appendSuper(super.toString()).build();
  }

}
