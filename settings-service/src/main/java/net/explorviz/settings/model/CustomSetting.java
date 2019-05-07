package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import xyz.morphia.annotations.Entity;

@Type("CustomSetting") // -> UserPreference klingt eindeutiger
public class CustomSetting {

  @Id
  @xyz.morphia.annotations.Id
  private final CustumSettingId id;

  private final Object value;

  @JsonCreator
  public CustomSetting(@JsonProperty("userId") final String userId,
      @JsonProperty("settingId") final String settingId,
      @JsonProperty("value") final Object value) {
    super();
    this.id = new CustumSettingId(userId, settingId);
    this.value = value;
  }

  public String getUserId() {
    return this.id.userId;
  }

  public CustumSettingId getId() {
    return this.id;
  }

  public String getSettingId() {
    return this.id.settingId;
  }

  public Object getValue() {
    return this.value;
  }


  @Entity(noClassnameStored = true)
  public static class CustumSettingId {

    private final String userId;
    private final String settingId;

    public CustumSettingId(final String userId, final String settingId) {
      this.userId = userId;
      this.settingId = settingId;
    }

    public String getUserId() {
      return this.userId;
    }

    public String getSettingId() {
      return this.settingId;
    }



  }


}
