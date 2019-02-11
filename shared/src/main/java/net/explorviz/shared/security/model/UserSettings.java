package net.explorviz.shared.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.BadRequestException;

/**
 * Model class for the user settings in the frontend.
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
    this.booleanAttributes = new HashMap<>();
    this.numericAttributes = new HashMap<>();
    this.stringAttributes = new HashMap<>();

    this.booleanAttributes.put("showFpsCounter", false);
    this.booleanAttributes.put("appVizTransparency", true);
    this.booleanAttributes.put("enableHoverEffects", true);
    this.booleanAttributes.put("keepHighlightingOnOpenOrClose", true);

    this.numericAttributes.put("appVizCommArrowSize", 1.0);
    this.numericAttributes.put("appVizTransparencyIntensity", 0.3);
  }

  public void put(final String attr, final boolean val) {
    this.booleanAttributes.put(attr, val);
  }

  public void put(final String attr, final Number val) {
    this.numericAttributes.put(attr, val);
  }

  public void put(final String attr, final String val) {
    this.stringAttributes.put(attr, val);
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  /*
   * Checks if the settings are valid
   */
  public void validate() {
    if (this.numericAttributes.containsKey("appVizCommArrowSize")
        && this.numericAttributes.get("appVizCommArrowSize").doubleValue() <= 0.0) {
      throw new BadRequestException("appVizCommArrowSize must be > 0");
    }
    if (this.numericAttributes.containsKey("appVizTransparencyIntensity")
        && (this.numericAttributes.get("appVizTransparencyIntensity").doubleValue() < 0.0
            || this.numericAttributes.get("appVizTransparencyIntensity").doubleValue() > 1.0)) {
      throw new BadRequestException("appVizTransparencyIntensity must be between 0.0 and 1.0");
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



}
