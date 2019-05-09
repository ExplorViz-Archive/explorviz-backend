package net.explorviz.settings.services;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserPreference;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.morphia.Datastore;

/**
 * This service is responsible for persisting and retrieving {@link Setting} objects. It is backed
 * by mongodb.
 *
 */
@Service
public class UserPreferenceRepository
    implements MongoRepository<UserPreference, UserPreference.CustomSettingId> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SettingsRepository.class.getSimpleName());


  private final Datastore datastore;



  @Inject
  public UserPreferenceRepository(final Datastore datastore) {
    this.datastore = datastore;
  }

  @Override
  public List<UserPreference> findAll() {
    return this.datastore.find(UserPreference.class).asList();

  }

  @Override
  public Optional<UserPreference> find(final UserPreference.CustomSettingId id) {
    final UserPreference u = this.datastore.get(UserPreference.class, id);
    return Optional.ofNullable(u);
  }

  @Override
  public void create(final UserPreference setting) {
    System.out.println(setting);
    this.datastore.save(setting);
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Created new user setting with id %s:%s", setting.getUserId(),
          setting.getSettingId()));
    }
  }

  @Override
  public void delete(final UserPreference.CustomSettingId id) {
    this.datastore.delete(this.datastore.find(UserPreference.class)
        .filter("_id.userId == ", id.getUserId()).filter("_id.settingId == ", id.getSettingId()));
  }



}
