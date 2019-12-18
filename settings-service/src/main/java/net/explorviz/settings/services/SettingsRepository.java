package net.explorviz.settings.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.querying.QueryResult;
import net.explorviz.shared.querying.Queryable;
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
public class SettingsRepository implements MongoRepository<Setting, String>, Queryable<Setting> {


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

    final List<Setting> all = new ArrayList<>();
    for (final Class<? extends Setting> settingCls : Setting.TYPES) {
      all.addAll(this.datastore.find(settingCls).asList());
    }

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

  @Override
  public QueryResult<Setting> query(final net.explorviz.shared.querying.Query<Setting> query) {
    final String originField = "origin";
    final String typeField = "type";

    String originFilter = null;


    List<Class<? extends Setting>> classes = new ArrayList<>(Setting.TYPES);

    if (query.getFilters().get(typeField) != null) {
      if (query.getFilters().get(typeField).size() == 1) {
        // Reduce the subclasses to process to only contain the class filtered for
        final String typeFilter = query.getFilters().get(typeField).get(0);
        classes = classes.stream()
            .filter(c -> c.getSimpleName().toLowerCase().contentEquals(typeFilter))
            .collect(Collectors.toList());
      } else {
        // Filters work conjunctive and settings can only be of a single type
        // thus the query result is empty
        return new QueryResult<>(query, new ArrayList<Setting>(), 0);
      }
    }

    if (query.getFilters().get(originField) != null) {
      if (query.getFilters().get(originField).size() == 1) {
        originFilter = query.getFilters().get(originField).get(0);
      } else {
        // Filters work conjunctive and settings can only be of a origin type
        // thus the query result is empty
        return new QueryResult<>(query, new ArrayList<Setting>(), 0);
      }
    }


    List<Setting> data = new ArrayList<>();


    for (final Class<?> cls : classes) {
      final xyz.morphia.query.Query<Setting> q = (Query<Setting>) this.datastore.createQuery(cls);
      if (originFilter != null) {
        q.field(originField).contains(originFilter);
      }
      data.addAll(q.asList());
    }

    final int total = data.size();

    if (query.doPaginate()) {
      final int from = Math.min(query.getPageNumber() * query.getPageSize(), data.size());
      final int to =
          Math.min(query.getPageNumber() * query.getPageSize() + query.getPageSize(), data.size());
      data = data.subList(from, to);
    }

    return new QueryResult<>(query, data, total);
  }


}
