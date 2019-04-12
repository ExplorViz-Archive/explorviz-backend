package net.explorviz.settings.services;


import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import net.explorviz.settings.model.Setting;
import net.explorviz.shared.security.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.morphia.Datastore;
import xyz.morphia.query.Query;

public class SettingsPersistenceService {

  
  private static final Logger LOGGER = LoggerFactory.getLogger(SettingsPersistenceService.class.getSimpleName());

  
  
  private final Datastore datastore;
  
  @Inject
  public SettingsPersistenceService(Datastore datastore) {
    this.datastore = datastore;
  }

  /**
   * Queries all settings
   * @return a list of all settings
   */
  public List<Setting> getAll() {
    Query<Setting> q = this.datastore.find(Setting.class);
    return q.asList();
  }
  
  /**
   * Queries a single setting
   * @param id the id
   * @return the setting
   */
  public Optional<Setting> getById(String id) {
    Setting s = this.datastore.get(Setting.class, id);
    return Optional.ofNullable(s);
  }
  
  /**
   * Removes a setting with a given id 
   * @param id the id
   */
  public void remove(String id) {
    this.datastore.delete(Setting.class, id);
  }
  
  /**
   * Creates a new setting
   * @param setting the setting to create
   */
  public void create(Setting setting) {
    this.datastore.save(setting);
  }
  
}
