package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.Property;
import xyz.morphia.annotations.Reference;

/**
 * Represents a specific setting a user has set a value other than the default value for 
 */
@Entity("UserSetting")
public class UserSetting<T> {

  @Id
  private final UserSetting.UserSettingId id;
  
  // the value
  private final T value;
  
  
  /**
   * Creates a new user setting.
   * @param userId the id of the user
   * @param setting the setting object
   * @param value the value for this setting
   */
  public UserSetting(String userId, String settingId, T value) {
    this.id = new UserSettingId(settingId, userId);
    this.value = value;
  }


  public String getSettingId() {
    return id.settingId;
  }


  public String getUserId() {
    return id.userId;
  }


  public Object getValue() {
    return value;
  }

  /**
   * 
   * A user setting is identified by both by the id of the user and the id of the setting.
   * This class functions as a composite key
   *
   */
  @Entity(noClassnameStored = true)
  private class UserSettingId {
    
    @Property("settingId")
    private String settingId;
    @Property("userId")
    private String userId;
    
    private UserSettingId(String settingId, String userId) {
      this.settingId = settingId;
      this.userId = userId;
    }

    
    public UserSettingId() {
      // Serializing
    }
    
    
    
  }

  
}
