package net.explorviz.settings.services;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import net.explorviz.settings.model.CustomSetting;
import net.explorviz.settings.model.Setting;
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
public class CustomSettingsRepository
    implements MongoRepository<CustomSetting, CustomSetting.CustomSettingId> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SettingsRepository.class.getSimpleName());


  private final Datastore datastore;



  @Inject
  public CustomSettingsRepository(final Datastore datastore) {
    this.datastore = datastore;
  }

  @Override
  public List<CustomSetting> findAll() {
    return this.datastore.find(CustomSetting.class).asList();

  }

  @Override
  public Optional<CustomSetting> find(final CustomSetting.CustomSettingId id) {
    final CustomSetting u = this.datastore.get(CustomSetting.class, id);
    return Optional.ofNullable(u);
  }

  @Override
  public void create(final CustomSetting setting) {
    System.out.println(setting);
    this.datastore.save(setting);
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Created new user setting with id %s:%s", setting.getUserId(),
          setting.getSettingId()));
    }
  }

  @Override
  public void delete(final CustomSetting.CustomSettingId id) {
    this.datastore.delete(this.datastore.find(CustomSetting.class)
        .filter("_id.userId == ", id.getUserId()).filter("_id.settingId == ", id.getSettingId()));
  }



}
