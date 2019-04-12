package net.explorviz.settings.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Reference;

/**
 * Represents a specific setting a user has set a value other than the default value for 
 */
@Entity("UserSetting")
public class UserSetting<T> {

  // the setting
  @Reference("Setting")
  @JsonIgnore
  private final String settingId;
  
  // the user id
  private final String userId;
  
  // the value
  private final T value;
  
  
  /**
   * Creates a new user setting.
   * @param userId the id of the user
   * @param setting the setting object
   * @param value the value for this setting
   */
  public UserSetting(String userId, String settingId, T value) {
    this.settingId = settingId;
    this.userId = userId;
    this.value = value;
  }


  public String getSettingId() {
    return settingId;
  }


  public String getUserId() {
    return userId;
  }


  public Object getValue() {
    return value;
  }

  
  private class UserSettingId {
    
    
    
  }

  
}
