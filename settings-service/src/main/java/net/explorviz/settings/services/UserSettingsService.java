package net.explorviz.settings.services;

import java.util.List;
import javax.inject.Inject;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.morphia.Datastore;

/**
 * This service is responsible for persisting and retrieving {@link Setting} objects. 
 * It is backed by mongodb.
 * 
 */
public class UserSettingsService {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(SettingsService.class.getSimpleName());
  
  
  private final Datastore datastore;
  
  @Inject
  public UserSettingsService(Datastore datastore) {
    this.datastore = datastore;
  }
  
  public List<UserSetting> findAll(String userId) {
    return null;
    
  }
  
  public UserSetting findById(String userId, String settingId) {
    return null;
  }
  
  public void save(UserSetting setting) {
    return;
  }
  
  public void delete(String userId, String settingId) {
    return;
  }
   
  
}
