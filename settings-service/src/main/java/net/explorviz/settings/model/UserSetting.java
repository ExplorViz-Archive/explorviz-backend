package net.explorviz.settings.model;

import com.github.jasminb.jsonapi.annotations.Type;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.Property;

/**
 * Represents a specific setting a user has set a value other than the default value for 
 */
@Entity("usersetting")
@Type("usersetting")
public class UserSetting{

  @Id
  @com.github.jasminb.jsonapi.annotations.Id
  private  UserSetting.UserSettingId id;
  
  // the value
  private Object value;
  
  // ugly but needed to infer the actual type at runtime as this class has to be 
  // deserialized without morphia, as it can't handle generic type
  // instead this class is deserialized with services.mongo.UserSettingCodec
  private Class<?> type;
  
  public UserSetting() {  }
  
  /**
   * Creates a new user setting.
   * @param userId the id of the user
   * @param setting the setting object
   * @param value the value for this setting
   */
  public UserSetting(String userId, String settingId, Object value) {
   
    if (userId == null || settingId == null) {
      throw new NullPointerException("Ids can't be null");
    }
    
    if (value == null) {
      throw new NullPointerException("Value can't be null");
    }
    
    this.id = new UserSettingId(userId, settingId);
    this.value = value;
   
    setType(value);
  }
  


  private void setType(Object value) {
    if (value instanceof String) {
      type =  String.class;
    } else if (value instanceof Double) {
      type =  Double.class;
    } else if (value instanceof Boolean) {
      type =  Boolean.class;
    } else {
      throw new IllegalArgumentException(String.format("Unknown setting type: %s", value.getClass().toString()));
    }
  }

  public UserSettingId getId() {
    return id;
  }


  public Object getValue() {
    return value;
  }

  public Class<?> getType() {
    return type;
  }
  
  @Override
  public String toString() {
    return new ToStringBuilder(this).append(this.id).append(this.value).build();
  }

  


  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    
    if (!(obj instanceof UserSetting)) {
      return false;
    }
    
    UserSetting rhs = (UserSetting) obj;
    
    return new EqualsBuilder().append(this.id, rhs.id)
        .append(this.getValue(), rhs.getValue())
        .build();
    
  }






  /**
   * 
   * A user setting is identified by both by the id of the user and the id of the setting.
   * This class functions as a composite key
   *
   */
  @Entity(noClassnameStored = true)
  public static class UserSettingId {
    
    @Property("settingId")
    private String settingId;
    @Property("userId")
    private String userId;
    
    public UserSettingId(String userId, String settingId) {
      this.settingId = settingId;
      this.userId = userId;
    }

    
    public UserSettingId() {
      // Serializing
    }


    public String getSettingId() {
      return settingId;
    }


    public void setSettingId(String settingId) {
      this.settingId = settingId;
    }


    public String getUserId() {
      return userId;
    }


    public void setUserId(String userId) {
      this.userId = userId;
    }


    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      
      if (!(obj instanceof UserSetting.UserSettingId)) {
        return false;
      }
      
      UserSetting.UserSettingId rhs = (UserSetting.UserSettingId) obj;
      
      return new EqualsBuilder()
          .append(this.settingId, rhs.settingId)
          .append(this.userId, rhs.userId)
          .build();
    }


    @Override
    public String toString() {
      return new ToStringBuilder(this).append(this.userId).append(this.settingId).build();
    }
        
    
  }

  
}
