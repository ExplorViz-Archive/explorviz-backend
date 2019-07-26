package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.github.jasminb.jsonapi.annotations.Id;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Base class for all settings.
 *
 */

@Schema(description = "Base class of settings", subTypes = {RangeSetting.class, FlagSetting.class},
    oneOf = {RangeSetting.class, FlagSetting.class})
@com.github.jasminb.jsonapi.annotations.Type("setting")
@JsonSubTypes({@Type(name = "RangeSetting", value = RangeSetting.class),
    @Type(name = "FlagSeting", value = FlagSetting.class)})
public abstract class Setting {

  @Id
  @xyz.morphia.annotations.Id
  protected String id;

  @JsonProperty
  protected String displayName;

  @JsonProperty
  protected String description;

  @JsonProperty
  protected String origin;

  /**
   * Creates a new Setting.
   * 
   * @param id the unique id
   * @param displayName the display name
   * @param description a brief description
   * @param origin the origin
   */
  public Setting(final String id, final String displayName, final String description,
      final String origin) {
    this.id = id;
    this.displayName = displayName;
    this.description = description;
    this.origin = origin;
  }

  /**
   * Creates a new Setting.
   * 
   * @param displayName the display name
   * @param description a brief description
   * @param origin the origin
   */
  public Setting(final String displayName, final String description, final String origin) {
    this.id = null;
    this.displayName = displayName;
    this.description = description;
    this.origin = origin;
  }

  public Setting() {
    // Morphia
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", this.id)
        .append("displayName", this.displayName)
        .append("description", this.description)
        .append("valueDescription")
        .build();
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public String getDescription() {
    return this.description;
  }

  public String getOrigin() {
    return this.origin;
  }



}
