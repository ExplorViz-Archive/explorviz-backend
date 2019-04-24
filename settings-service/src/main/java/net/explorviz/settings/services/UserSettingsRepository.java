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
public class UserSettingsRepository implements MongoRepository<UserSetting, UserSetting.UserSettingId> {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(SettingsRepository.class.getSimpleName());
  
  
  private final Datastore datastore;
  
  @Inject
  public UserSettingsRepository(Datastore datastore) {
    this.datastore = datastore;
  }
  
  @Override
  public List<UserSetting> findAll() {
    Query<UserSetting> q = this.datastore.find(UserSetting.class);
    
    return q.asList();    
  }
  
  @Override
  public Optional<UserSetting> find(UserSetting.UserSettingId id) {
    UserSetting u = datastore.get(UserSetting.class, new UserSetting.UserSettingId(id.getUserId(), id.getSettingId()));
    return Optional.ofNullable(u);
  }
  
  @Override
  public void create(UserSetting setting) {
    datastore.save(setting);
    if(LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Created new user setting with id %s", setting.getId()));
    }
  }
  
  @Override
  public void delete(UserSetting.UserSettingId id) {
    datastore.delete(UserSetting.class, new UserSetting.UserSettingId(id.getUserId(), id.getSettingId()));
  }


   
  
}
