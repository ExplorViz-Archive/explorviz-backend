package net.explorviz.settings.services;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserSetting;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.morphia.Datastore;
import xyz.morphia.query.Query;

/**
 * This service is responsible for persisting and retrieving {@link Setting} objects. 
 * It is backed by mongodb.
 * 
 */
@Service
public class UserSettingsRepository {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(SettingsRepository.class.getSimpleName());
  
  
  private final Datastore datastore;
  
  @Inject
  public UserSettingsRepository(Datastore datastore) {
    this.datastore = datastore;
  }
  
  public List<UserSetting> findAll(String userId) {
    Query<UserSetting> q = this.datastore.find(UserSetting.class).filter("userId", userId);
    return q.asList();    
  }
  
  public Optional<UserSetting> findById(String userId, String settingId) {
    UserSetting u = datastore.get(UserSetting.class, new UserSetting.UserSettingId(userId, settingId));
    return Optional.ofNullable(u);
  }
  
  public void save(UserSetting setting) {
    datastore.save(setting);
  }
  
  public void delete(String userId, String settingId) {
    datastore.delete(UserSetting.class, new UserSetting.UserSettingId(userId, settingId));
  }
   
  
}
