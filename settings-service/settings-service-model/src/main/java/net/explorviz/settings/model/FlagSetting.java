package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Type;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Type("FlagSetting")
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


  public boolean getDefaultValue() {
    return this.defaultValue;
  }

  public FlagSetting() {
    // Morphia
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
