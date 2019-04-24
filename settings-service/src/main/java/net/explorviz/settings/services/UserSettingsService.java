package net.explorviz.settings.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.explorviz.settings.model.BooleanSetting;
import net.explorviz.settings.model.DoubleSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.StringSetting;
import net.explorviz.settings.model.UserSetting;
import net.explorviz.settings.model.UserSetting.UserSettingId;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles settings for users and automatically keeps track of orphaned settings.
 *
 */
@Service
public class UserSettingsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserSettingsService.class.getSimpleName());
  
  private MongoRepository<Setting, String>  settingRepo;
  
  private MongoRepository<UserSetting, UserSetting.UserSettingId> userSettingRepo;
  
  
  @Inject
  public UserSettingsService(MongoRepository<Setting, String> settingsRepo, MongoRepository<UserSetting, UserSetting.UserSettingId> userSettingsRepo) {
    this.settingRepo = settingsRepo;
    this.userSettingRepo = userSettingsRepo;
  }
  
  /**
   * Returns a key value map representing the settings for a single user
   * @param userId id of the user
   * @return the map
   */
  public Map<String, Object> getForUser(String userId) {
    Map<String, Object> ret = new HashMap<String, Object>();
    
    // Default settings
    settingRepo.findAll().forEach(s -> ret.put(s.getId(), s.getDefaultValue()));
    
    // Automatic orphan removal
    
    List<UserSetting> userSettings = userSettingRepo.findAll().stream().filter(us -> us.getId().getUserId().equals(userId)).collect(Collectors.toList());
    removeOrphans(userSettings);
    
    // Override defaults with user specific settings
    userSettings.forEach(us -> ret.put(us.getId().getSettingId(), us.getValue()));
    
    return ret;
  }
  
  /**
   * 
   * @param userId the user's id
   * @param settingId the setting's id
   * @param value the value 
   * @throws IllegalArgumentException if the type of the value does not match the type of the setting
   * @throws UnknownSettingException if the setting does not exist
   */
  public void setForUser(String userId, String settingId, Object value) throws IllegalArgumentException, UnknownSettingException {
    
    // Check if setting exists
    Setting s = settingRepo.find(settingId).orElseThrow(IllegalArgumentException::new);
    UserSetting u = null;
    
    if (value == null) {
      throw new NullPointerException();
    }
    
    if (s instanceof BooleanSetting) {
      BooleanSetting setting = (BooleanSetting) s;
      if (value instanceof Boolean) {
        u = new UserSetting(userId, settingId, value);
      } else {
        throw new IllegalArgumentException("Setting and value type don't match");
      }
    } else if (s instanceof StringSetting) {
      StringSetting setting = (StringSetting) s;
      if (value instanceof String) {
        u = new UserSetting(userId, settingId, value);
      } else {
        throw new IllegalArgumentException("Setting and value type don't match");
      }
      
    } else if (s instanceof DoubleSetting) {
      DoubleSetting setting = (DoubleSetting) s;
      if (value instanceof Double) {
        u = new UserSetting(userId, settingId, value);
      } else {
        throw new IllegalArgumentException("Setting and value type don't match");
      }
      
    } else {
      throw new UnknownSettingException();
    }
    
    userSettingRepo.create(u);
        
    
  }
  
  /**
   * Sets a setting for a specific user to its default value
   * @param userId the id of the user
   * @param settingId the id of the setting
   */
  public void setDefault(String userId, String settingId) {
    userSettingRepo.delete(new UserSettingId(userId, settingId));
  }
    
  /**
   * Removes orphans (i.e. deleted settings that still exists in a users settings) from the user settings an propagets the deletions to the persistence.
   * @param userSettings the user settings
   * @param settings the default settings
   */
  private void removeOrphans(List<UserSetting> userSettings) {
    Iterator<UserSetting> it = userSettings.iterator();
    while(it.hasNext()) {
      UserSetting us = it.next();
      String usid = us.getId().getSettingId();
      
      if(!settingRepo.find(usid).isPresent()) {
        // found orphanized user setting
        // remove from setting list
        if (LOGGER.isInfoEnabled()) {
          LOGGER.info(String.format("Found orphanized setting: ", usid));
        }
        userSettings.remove(us);
        // remove from database
        userSettingRepo.delete(us.getId());
        
      }
      
    }
  }
  
  
}