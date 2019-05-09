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

@Type("CustomSetting") // -> UserPreference klingt eindeutiger
@Converters(CustomSettingConverter.class)
@Entity("CustomSetting")
public class CustomSetting {

  @Id
  @org.mongodb.morphia.annotations.Id
  @Embedded
  private CustomSettingId id;

  private Object value;

  @JsonCreator
  public CustomSetting(@JsonProperty("userId") final String userId,
      @JsonProperty("settingId") final String settingId,
      @JsonProperty("value") final Object value) {
    super();
    this.id = new CustomSettingId(userId, settingId);
    this.value = value;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", this.id).append(this.value).build();
  }

  @PreLoad
  void fixup(final DBObject obj) {
    /*
     * this fixes morphia trying to cast value to a DBObject, which will fail in case of a primitive
     * Type (i.e. an int can't be cast to DBObject). Thus we just take the raw value.
     */
    this.value = obj.get("value");
    obj.removeField("value");
  }

  public CustomSetting() {
    // Morphia
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


  @Entity(noClassnameStored = true)
  public static class CustomSettingId {

    private String userId;
    private String settingId;

    public CustomSettingId(final String userId, final String settingId) {
      this.userId = userId;
      this.settingId = settingId;
    }

    public String getUserId() {
      return this.userId;
    }

    public String getSettingId() {
      return this.settingId;
    }

    public CustomSettingId() {
      // Morphia
    }

    @Override
    public String toString() {
      return new ToStringBuilder(this).append("userId", this.userId)
          .append("settingId", this.settingId).build();
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj != null) {
        if (obj instanceof CustomSetting.CustomSettingId) {
          final CustomSettingId rhs = (CustomSettingId) obj;
          return new EqualsBuilder().append(this.userId, rhs.userId)
              .append(this.settingId, rhs.settingId).build();
        }
      }
      return false;
    }

  }


}

