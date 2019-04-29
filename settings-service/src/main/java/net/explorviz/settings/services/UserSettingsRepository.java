package net.explorviz.settings.services;

import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserSetting;
import net.explorviz.settings.services.mongo.MongoHelper;
import net.explorviz.settings.services.mongo.UserSettingCodec;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
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
public class UserSettingsRepository
    implements MongoRepository<UserSetting, UserSetting.UserSettingId> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SettingsRepository.class.getSimpleName());


  private final MongoHelper mongoHelper;
  private final Datastore datastore;



  @Inject
  public UserSettingsRepository(final MongoHelper mongoHelper, final Datastore datastore) {
    this.mongoHelper = mongoHelper;
    this.datastore = datastore;
  }

  @Override
  public List<UserSetting> findAll() {
    final CodecRegistry registry = CodecRegistries.fromCodecs(new UserSettingCodec());
    final MongoCollection<Document> userSettingCollection =
        this.mongoHelper.getUserSettingsCollection();
    final Iterator<UserSetting> it =
        userSettingCollection.withCodecRegistry(registry).find(UserSetting.class).iterator();

    final List<UserSetting> result = new ArrayList<UserSetting>();
    it.forEachRemaining(result::add);

    return result;

  }

  @Override
  public Optional<UserSetting> find(final UserSetting.UserSettingId id) {
    final UserSetting u = this.datastore.get(UserSetting.class,
        new UserSetting.UserSettingId(id.getUserId(), id.getSettingId()));
    return Optional.ofNullable(u);
  }

  @Override
  public void create(final UserSetting setting) {
    this.datastore.save(setting);
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Created new user setting with id %s", setting.getId()));
    }
  }

  @Override
  public void delete(final UserSetting.UserSettingId id) {
    this.datastore.delete(this.datastore.find(UserSetting.class)
        .filter("_id.userId == ", id.getUserId()).filter("_id.settingId == ", id.getSettingId()));
  }



}
