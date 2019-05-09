package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import com.mongodb.DBObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mongodb.morphia.annotations.Converters;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.PreLoad;

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
  @org.mongodb.morphia.annotations.Id
  @Embedded
  private CustomSettingId id;

  private Object value;

  /**
   * Creates a new user preference.
   * 
   * @param userId the id of the user this preference belongs to
   * @param settingId the id of the associated setting
   * @param value the value the given user has set for the given setting
   */
  @JsonCreator
  public UserPreference(@JsonProperty("userId") final String userId,
      @JsonProperty("settingId") final String settingId,
      @JsonProperty("value") final Object value) {
    super();
    this.id = new CustomSettingId(userId, settingId);
    this.value = value;
  }
  
  @SuppressWarnings("unused")
  private UserPreference() {
    // Morphia
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", this.id).append(this.value).build();
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

 

  public String getUserId() {
    return this.id.userId;
  }

  public CustomSettingId getId() {
    return this.id;
  }

  public String getSettingId() {
    return this.id.settingId;
  }

  public Object getValue() {
    return this.value;
  }

  /**
   * A user preference is identified by the user it belongs to as well as the setting 
   * its associated to.
   * This class represents the corresponding composite key.
   */
  @Entity(noClassnameStored = true)
  public static class CustomSettingId {

    private String userId;
    private String settingId;

    public CustomSettingId(final String userId, final String settingId) {
      this.userId = userId;
      this.settingId = settingId;
    }

    @SuppressWarnings("unused")
    private CustomSettingId() {
      // Morphia
    }
    
    public String getUserId() {
      return this.userId;
    }

    public String getSettingId() {
      return this.settingId;
    }

    

    @Override
    public String toString() {
      return new ToStringBuilder(this).append("userId", this.userId)
          .append("settingId", this.settingId).build();
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj != null) {
        if (obj instanceof UserPreference.CustomSettingId) {
          final CustomSettingId rhs = (CustomSettingId) obj;
          return new EqualsBuilder().append(this.userId, rhs.userId)
              .append(this.settingId, rhs.settingId).build();
        }
      }
      return false;
    }
    
    @Override
    public int hashCode() {
      return super.hashCode();
    }
    

  }


}

