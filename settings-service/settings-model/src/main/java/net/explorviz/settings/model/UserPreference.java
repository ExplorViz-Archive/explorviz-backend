package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import com.mongodb.DBObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import xyz.morphia.annotations.Converters;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.PreLoad;

/**
 * An entity of this class represent the preference of a user regarding a specific {@link Setting}.
 * With UserPreferences the default values of setting are overridden for a given user.
 *
 */
@Type("userpreference") //
@Converters(UserPreferenceConverter.class)
@Entity("UserPreference")
public class UserPreference {

  private static final String VALUE = "value";

  @Id
  @xyz.morphia.annotations.Id
  private String id;

  private String userId;
  private String settingId;

  private Object value;



  /**
   * Creates a new user preference.
   * 
   * @param userId the id of the user this preference belongs to
   * @param settingId the id of the associated setting
   * @param value the value the given user has set for the given setting
   */
  @JsonCreator
  public UserPreference(@JsonProperty("id") final String id,
      @JsonProperty("userid") final String userId,
      @JsonProperty("settingid") final String settingId,
      @JsonProperty("value") final Object value) {
    super();
    this.id = id;
    this.value = value;
    this.userId = userId;
    this.settingId = settingId;
  }

  @SuppressWarnings("unused")
  private UserPreference() {
    // Morphia
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", this.id)
        .append("userId", this.userId)
        .append("settingId", this.settingId)
        .append(this.value)
        .build();
  }

  @PreLoad
  void fixup(final DBObject obj) {
    /*
     * this fixes morphia trying to cast value to a DBObject, which will fail in case of a primitive
     * Type (i.e. an integer can't be cast to DBObject). Thus we just take the raw value.
     */
    this.value = obj.get(VALUE);
    obj.removeField(VALUE);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final UserPreference that = (UserPreference) o;

    return new EqualsBuilder().append(this.id, that.id)
        .append(this.userId, that.userId)
        .append(this.settingId, that.settingId)
        .append(this.value, that.value)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(this.id)
        .append(this.userId)
        .append(this.settingId)
        .append(this.value)
        .toHashCode();
  }

  public String getUserId() {
    return this.userId;
  }

  public String getId() {
    return this.id;
  }

  public String getSettingId() {
    return this.settingId;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public Object getValue() {
    return this.value;
  }

  public void setValue(final Object value) {
    this.value = value;
  }

}

