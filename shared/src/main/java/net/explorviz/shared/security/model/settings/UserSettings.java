package net.explorviz.shared.security.model.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Model class for the user settings in the frontend.
 */
/**
 * @author lotzk
 *
 */
@Type("usersetting")
public class UserSettings {

  @Id(LongIdHandler.class)
  private Long id = 1L;

  @JsonSerialize
  @JsonProperty("booleanAttributes")
  private final Map<String, Boolean> booleanAttributes;

  @JsonSerialize
  @JsonProperty("numericAttributes")
  private final Map<String, Number> numericAttributes;

  @JsonSerialize
  @JsonProperty("stringAttributes")
  private final Map<String, String> stringAttributes;


  public UserSettings() {
    this.booleanAttributes = DefaultSettings.DEFAULT_BOOLEAN_SETTINGS();
    this.numericAttributes = DefaultSettings.DEFAULT_NUMERIC_SETTINGS();
    this.stringAttributes = DefaultSettings.DEFAULT_STRING_SETTINGS();

  }

  public void put(final String attr, final boolean val) {
    if (this.booleanAttributes.containsKey(attr)) {
      this.booleanAttributes.put(attr, val);
    } else {
      throw new IllegalArgumentException(String.format("Setting with key %s not found", attr));
    }
  }

  public void put(final String attr, final Number val) {
    if (this.numericAttributes.containsKey(attr)) {
      this.numericAttributes.put(attr, val);
    } else {
      throw new IllegalArgumentException(String.format("Setting with key %s not found", attr));
    }
  }

  public void put(final String attr, final String val) {
    if (this.stringAttributes.containsKey(attr)) {
      this.stringAttributes.put(attr, val);
    } else {
      throw new IllegalArgumentException(String.format("Setting with key %s not found", attr));
    }
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public boolean getBooleanAttribute(final String key) {
    return this.booleanAttributes.get(key);
  }

  public Number getNumericAttribute(final String key) {
    return this.numericAttributes.get(key);
  }

  public String getStringAttribute(final String key) {
    return this.stringAttributes.get(key);
  }



  /*
   * Checks if the settings are valid
   */
  public void validate() {

    // Check whether these settings contain unkown keys
    if (!this.numericAttributes.keySet().equals(DefaultSettings.DEFAULT_NUMERIC_SETTINGS().keySet())
        || !this.booleanAttributes.keySet()
            .equals(DefaultSettings.DEFAULT_BOOLEAN_SETTINGS().keySet())
        || !this.stringAttributes.keySet()
            .equals(DefaultSettings.DEFAULT_STRING_SETTINGS().keySet())) {
      throw new IllegalStateException("Contain unknown settings");
    }

    if (this.numericAttributes.containsKey("appVizCommArrowSize")
        && this.numericAttributes.get("appVizCommArrowSize").doubleValue() <= 0.0) {
      throw new IllegalStateException("appVizCommArrowSize must be > 0");
    }
    if (this.numericAttributes.containsKey("appVizTransparencyIntensity")
        && (this.numericAttributes.get("appVizTransparencyIntensity").doubleValue() < 0.0
            || this.numericAttributes.get("appVizTransparencyIntensity").doubleValue() > 1.0)) {
      throw new IllegalStateException("appVizTransparencyIntensity must be between 0.0 and 1.0");
    }
  }


  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof UserSettings)) {
      return false;
    }
    final UserSettings otherObj = (UserSettings) obj;

    return this.id.equals(otherObj.getId()) && this.booleanAttributes != null
        && this.numericAttributes != null && this.stringAttributes != null
        && this.booleanAttributes.equals(otherObj.booleanAttributes)
        && this.numericAttributes.equals(otherObj.numericAttributes)
        && this.stringAttributes.equals(otherObj.stringAttributes);

  }

  @Override
  public String toString() {

    final ToStringBuilder tsb = new ToStringBuilder(this);
    tsb.append("numeric attributes", this.numericAttributes);
    tsb.append("boolean attributes", this.booleanAttributes);
    tsb.append("string attributes", this.stringAttributes);

    return tsb.build();

  }



}
