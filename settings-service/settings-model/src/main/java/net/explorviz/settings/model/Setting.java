package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.github.jasminb.jsonapi.annotations.Id;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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


  /**
   * Contains all Subclasses of Setting, i.e., all concrete implementations of a Setting.
   */
  @SuppressWarnings("serial")
  public static final List<Class<? extends Setting>> TYPES =
      new ArrayList<Class<? extends Setting>>() {
        {
          this.add(RangeSetting.class);
          this.add(FlagSetting.class);
        }
      };


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

  public void setId(final String id) {
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

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final Setting setting = (Setting) o;

    return new EqualsBuilder().append(this.id, setting.id)
        .append(this.displayName, setting.displayName)
        .append(this.description, setting.description)
        .append(this.origin, setting.origin)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(this.id)
        .append(this.displayName)
        .append(this.description)
        .append(this.origin)
        .toHashCode();
  }
}
