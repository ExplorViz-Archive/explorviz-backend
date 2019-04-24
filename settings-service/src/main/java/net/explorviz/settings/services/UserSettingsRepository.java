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
import xyz.morphia.query.Query;

/**
 * This service is responsible for persisting and retrieving {@link Setting} objects. 
 * It is backed by mongodb.
 * 
 */
@Service
public class UserSettingsRepository implements MongoRepository<UserSetting, UserSetting.UserSettingId> {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(SettingsRepository.class.getSimpleName());
  
  
  private final MongoHelper mongoHelper;
  private final Datastore datastore;
  
  
  
  @Inject
  public UserSettingsRepository(MongoHelper mongoHelper, Datastore datastore) {
    this.mongoHelper = mongoHelper;
    this.datastore = datastore;
  }
  
  @Override
  public List<UserSetting> findAll() {
    CodecRegistry registry =  CodecRegistries.fromCodecs(new UserSettingCodec());
    MongoCollection<Document> userSettingCollection = this.mongoHelper.getUserSettingsCollection();
    Iterator<UserSetting> it = userSettingCollection.withCodecRegistry(registry).find(UserSetting.class).iterator();
    
    List<UserSetting> result = new ArrayList<UserSetting>();
    it.forEachRemaining(result::add);
       
    return result;
      
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
    datastore.delete(datastore.find(UserSetting.class).filter("_id.userId == ", id.getUserId()).filter("_id.settingId == ", id.getSettingId()));
  }


   
  
}
