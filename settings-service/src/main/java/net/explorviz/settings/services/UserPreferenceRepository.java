package net.explorviz.settings.services;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.shared.common.idgen.IdGenerator;
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
public class UserPreferenceRepository implements MongoRepository<UserPreference, String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SettingsRepository.class);


  private final Datastore datastore;

  private final IdGenerator idgen;

  @Inject
  public UserPreferenceRepository(final Datastore datastore, final IdGenerator idgen) {
    this.datastore = datastore;
    this.idgen = idgen;
  }

  @Override
  public List<UserPreference> findAll() {
    return this.datastore.find(UserPreference.class).asList();

  }

  @Override
  public Optional<UserPreference> find(final String id) {
    final UserPreference u = this.datastore.get(UserPreference.class, id);
    return Optional.ofNullable(u);
  }

  @Override
  public UserPreference createOrUpdate(final UserPreference pref) {
    if (pref.getId() == null || pref.getId().isEmpty()) {
      // generate new id
      pref.setId(this.idgen.generateId());
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info(String.format("Created new user preference %s", pref.toString()));
      }
    }
    this.datastore.save(pref);

    return pref;
  }

  @Override
  public void delete(final String id) {
    this.datastore.delete(this.datastore.find(UserPreference.class).filter("_id == ", id));
  }



}
