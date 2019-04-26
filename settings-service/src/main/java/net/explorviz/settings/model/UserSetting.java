package net.explorviz.settings.model;

import com.github.jasminb.jsonapi.annotations.Type;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.Property;

/**
 * Represents a specific setting a user has set a value other than the default value for.
 */
@Entity("usersetting")
@Type("usersetting")
public class UserSetting {

  @Id
  @com.github.jasminb.jsonapi.annotations.Id
  private UserSetting.UserSettingId id;

  // the value
  private Object value;

  // ugly but needed to infer the actual type at runtime as this class has to be
  // deserialized without morphia, as it can't handle generic type
  // instead this class is deserialized with services.mongo.UserSettingCodec
  private Class<?> type;

  public UserSetting() {
    // for serialization
  }

  /**
   * Creates a new user setting.
   *
   * @param userId the id of the user
   * @param settingId the setting object
   * @param value the value for this setting
   */
  public UserSetting(final String userId, final String settingId, final Object value) {

    if (userId == null || settingId == null) {
      throw new NullPointerException("Ids can't be null");
    }

    if (value == null) {
      throw new NullPointerException("Value can't be null");
    }

    this.id = new UserSettingId(userId, settingId);
    this.value = value;

    this.setType(value);
  }



  private void setType(final Object value) {
    if (value instanceof String) {
      this.type = String.class;
    } else if (value instanceof Double) {
      this.type = Double.class;
    } else if (value instanceof Boolean) {
      this.type = Boolean.class;
    } else {
      throw new IllegalArgumentException(
          String.format("Unknown setting type: %s", value.getClass().toString()));
    }
  }

  public UserSettingId getId() {
    return this.id;
  }


  public Object getValue() {
    return this.value;
  }

  public Class<?> getType() {
    return this.type;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append(this.id).append(this.value).build();
  }



  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }

    if (!(obj instanceof UserSetting)) {
      return false;
    }

    final UserSetting rhs = (UserSetting) obj;

    return new EqualsBuilder().append(this.id, rhs.id).append(this.getValue(), rhs.getValue())
        .build();

  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(this.id).append(this.value).build();
  }



  /**
   * A user setting is identified by both by the id of the user and the id of the setting. This
   * class functions as a composite key.
   *
   */
  @Entity(noClassnameStored = true)
  public static class UserSettingId {

    @Property("settingId")
    private String settingId;
    @Property("userId")
    private String userId;

    public UserSettingId(final String userId, final String settingId) {
      this.settingId = settingId;
      this.userId = userId;
    }


    public UserSettingId() {
      // Serializing
    }


    public String getSettingId() {
      return this.settingId;
    }


    public void setSettingId(final String settingId) {
      this.settingId = settingId;
    }


    public String getUserId() {
      return this.userId;
    }


    public void setUserId(final String userId) {
      this.userId = userId;
    }


    @Override
    public boolean equals(final Object obj) {
      if (obj == null) {
        return false;
      }

      if (!(obj instanceof UserSetting.UserSettingId)) {
        return false;
      }

      final UserSetting.UserSettingId rhs = (UserSetting.UserSettingId) obj;

      return new EqualsBuilder().append(this.settingId, rhs.settingId)
          .append(this.userId, rhs.userId).build();
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder().append(this.settingId).append(this.userId).build();
    }


    @Override
    public String toString() {
      return new ToStringBuilder(this).append(this.userId).append(this.settingId).build();
    }


  }


}
