package net.explorviz.settings.services;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.shared.common.idgen.IdGenerator;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.morphia.Datastore;
import xyz.morphia.query.Query;


/**
 * This service is responsible for persisting and retrieving {@link Setting} objects. It is backed
 * by mongodb.
 *
 */
@Service
public class SettingsRepository implements MongoRepository<Setting, String> {


  private static final Logger LOGGER = LoggerFactory.getLogger(SettingsRepository.class);

  private final IdGenerator idgen;

  private final Datastore datastore;

  @Inject
  public SettingsRepository(final Datastore datastore, final IdGenerator idgen) {
    this.datastore = datastore;
    this.idgen = idgen;
  }

  /**
   * Queries all settings.
   *
   * @return a list of all settings
   */
  @Override
  public List<Setting> findAll() {
    // Morphia can't query for subtypes, this has to be done manually
    // See https://github.com/MorphiaOrg/morphia/issues/22
    final Query<RangeSetting> qRange = this.datastore.find(RangeSetting.class);
    final Query<FlagSetting> qFlags = this.datastore.find(FlagSetting.class);

    final List<Setting> all = new ArrayList<>();
    all.addAll(qRange.asList());
    all.addAll(qFlags.asList());

    return all;
  }

  /**
   * Queries a single setting.
   *
   * @param id the id
   * @return the setting
   */
  @Override
  public Optional<Setting> find(final String id) {
    // Problem: Each subtype is stored within a seperate collection, thus allowing ids to occur
    // multiple times
    if (id == null || id.isEmpty()) {
      return Optional.empty();
    }

    final Class<?>[] types = {RangeSetting.class, FlagSetting.class};

    for (final Class<?> t : types) {
      final Setting setting = (Setting) this.datastore.get(t, id);
      final Optional<Setting> res = Optional.ofNullable(setting);

      if (res.isPresent()) {
        return res;
      }
    }

    return Optional.empty();
  }

  /**
   * Removes a setting with a given id.
   *
   * @param id the id
   */
  @Override
  public void delete(final String id) {
    int n = 0;
    n += this.datastore.delete(RangeSetting.class, id).getN();
    n += this.datastore.delete(FlagSetting.class, id).getN();

    if (LOGGER.isInfoEnabled()) {
      if (n > 0) {
        LOGGER.info(String.format("Removed setting with id %s", id));
      } else {
        LOGGER.info(String.format("No setting with id %s}", id));
      }
    }
  }



  /**
   * Creates a new setting.
   *
   * @param setting the setting to create
   * @throws IllegalArgumentException if the setting has already an id assigned
   */
  @Override
  public Setting createOrUpdate(final Setting setting) {

    if (setting.getId() != null && !setting.getId().isEmpty()) {
      throw new IllegalArgumentException("This instance already has an id, can't create");
    }

    setting.setId(this.idgen.generateId());

    this.datastore.save(setting);
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Saved setting with id %s", setting.getId()));
    }

    return setting;
  }


  /**
   * Creates a new setting and overrides if there was a setting with such an id.
   *
   * @param setting the setting to create
   */
  public void createOrOverride(final Setting setting) {
    if (this.find(setting.getId()).isPresent() && LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Overwriting setting with id %s", setting.getId()));
    }
    this.datastore.save(setting);
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Saved setting with id %s", setting.getId()));
    }
  }


}
