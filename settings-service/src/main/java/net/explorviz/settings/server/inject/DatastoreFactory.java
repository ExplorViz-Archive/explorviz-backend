package net.explorviz.settings.server.inject;

import com.mongodb.MongoClient;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.model.UserPreferenceConverter;
import net.explorviz.shared.config.annotations.Config;
import org.glassfish.hk2.api.Factory;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;


public class DatastoreFactory implements Factory<Datastore> {

  private final Datastore datastore;

  /**
   * Creates new Datastore, which will be used for injection.
   *
   * @param host the host of the database
   * @param port the port of the database
   */
  @Config("mongo.host")
  @Config("mongo.port")
  public DatastoreFactory(final String host, final String port) {
    final Morphia morphia = new Morphia();
    morphia.map(Setting.class, RangeSetting.class, FlagSetting.class, UserPreference.class);
    morphia.getMapper().getConverters().addConverter(new UserPreferenceConverter());
    this.datastore = morphia.createDatastore(new MongoClient(host + ":" + port), "explorviz");
    this.datastore.ensureIndexes();

  }

  @Override
  public Datastore provide() {
    return this.datastore;
  }

  @Override
  public void dispose(final Datastore instance) {
    // TODO Auto-generated method stub
  }



}
