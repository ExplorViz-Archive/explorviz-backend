package net.explorviz.settings.services;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.querying.Query;
import net.explorviz.shared.querying.QueryResult;
import net.explorviz.shared.querying.Queryable;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.morphia.Datastore;
import xyz.morphia.query.FindOptions;

/**
 * This service is responsible for persisting and retrieving {@link Setting} objects. It is backed
 * by mongodb.
 *
 */
@Service
public class UserPreferenceRepository
    implements MongoRepository<UserPreference, String>, Queryable<UserPreference> {

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

  /**
   * {@inheritDoc}
   *
   * @throws IllegalStateException if a preference with the same userId and settingId already
   *         exists.
   */
  @Override
  public UserPreference createOrUpdate(final UserPreference pref) {


    if (pref.getId() == null || pref.getId().isEmpty()) {
      if (this.prefExist(pref.getUserId(), pref.getSettingId())) {
        throw new IllegalStateException("A preference for this user and setting already exists.");
      }
      // generate new id
      pref.setId(this.idgen.generateId());
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info(String.format("Created new user preference %s", pref.toString()));
      }
    }
    this.datastore.save(pref);

    return pref;
  }

  /**
   * Checks whether a preference for given user and setting exists.
   *
   * @param uid user id
   * @param sid setting id
   * @return
   */
  private boolean prefExist(final String uid, final String sid) {
    final long c = this.datastore.find(UserPreference.class)
        .filter("userId ==", uid)
        .filter("settingId ==", sid)
        .count();
    return c > 0;
  }

  @Override
  public void delete(final String id) {
    this.datastore.delete(this.datastore.find(UserPreference.class).filter("_id == ", id));
  }



  @Override
  public QueryResult<UserPreference> query(final Query<UserPreference> query) {
    final String userIdField = "userId";
    final String userFilterField = "user";

    final xyz.morphia.query.Query<UserPreference> q =
        this.datastore.createQuery(UserPreference.class);


    // Filter for user
    final List<String> userFilter = query.getFilters().get(userFilterField);

    if (userFilter != null && userFilter.size() == 1) {
      q.field(userIdField).contains(userFilter.get(0));
    }


    final FindOptions options = new FindOptions();
    if (query.doPaginate()) {
      options.limit(query.getPageSize());
      options.skip(query.getPageSize() * query.getPageNumber());
    }

    final long total = q.count();
    final List<UserPreference> data = q.asList(options);

    return new QueryResult<>(query, data, total);

  }



}
