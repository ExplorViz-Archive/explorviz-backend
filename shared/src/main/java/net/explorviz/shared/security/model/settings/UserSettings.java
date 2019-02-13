package net.explorviz.shared.security.model.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Model class for the user settings in the frontend.
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
  private final Map<String, Double> numericAttributes;

  @JsonSerialize
  @JsonProperty("stringAttributes")
  private final Map<String, String> stringAttributes;


  public UserSettings() {
    this.booleanAttributes = DefaultSettings.booleanDefaults();
    this.numericAttributes = DefaultSettings.numericDefaults();
    this.stringAttributes = DefaultSettings.stringDefaults();

  }

  public void put(final String attr, final boolean val) {
    this.booleanAttributes.put(attr, val);
  }

  public void put(final String attr, final double val) {
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



  public Map<String, Boolean> getBooleanAttributes() {
    return this.booleanAttributes;
  }

  public Map<String, Double> getNumericAttributes() {
    return this.numericAttributes;
  }

  public Map<String, String> getStringAttributes() {
    return this.stringAttributes;
  }

  /*
   * Checks if the settings are valid
   */
  public void validate() {

    // Check whether these settings contain unkown keys
    if (!this.numericAttributes.keySet().equals(DefaultSettings.numericDefaults().keySet())
        || !this.booleanAttributes.keySet().equals(DefaultSettings.booleanDefaults().keySet())
        || !this.stringAttributes.keySet().equals(DefaultSettings.stringDefaults().keySet())) {
      throw new IllegalStateException("Contains unknown settings");
    }


    // Check whether all numeric values are in range

    for (final Entry<String, Double> e : this.numericAttributes.entrySet()) {
      final double min = DefaultSettings.numericSettings().get(e.getKey()).getMin();
      final double max = DefaultSettings.numericSettings().get(e.getKey()).getMax();
      if (e.getValue() < min) {
        throw new IllegalStateException(
            String.format("Value of %s is smaller then minumum of %f", e.getKey(), min));
      }
      if (e.getValue() > max) {
        throw new IllegalStateException(
            String.format("Value of %s is greater then minumum of %f", e.getKey(), max));
      }
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
