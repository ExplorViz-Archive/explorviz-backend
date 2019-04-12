package net.explorviz.settings.server.inject;

import com.mongodb.MongoClient;
import net.explorviz.settings.model.BooleanSetting;
import net.explorviz.settings.model.DoubleSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.StringSetting;
import net.explorviz.settings.model.UserSetting;
import net.explorviz.shared.config.annotations.Config;
import org.glassfish.hk2.api.Factory;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;

public class DatastoreFactory implements Factory<Datastore>{

  private Datastore datastore;
  
  /**
   * Creates new Datastore, which will be used for injection.
   *
   * @param host
   * @param port
   */
  @Config("mongo.host")
  @Config("mongo.port")
  public DatastoreFactory(String host, String port) {
    Morphia morphia = new Morphia();
    morphia.map(Setting.class, DoubleSetting.class, StringSetting.class, BooleanSetting.class, UserSetting.class);
    this.datastore = morphia.createDatastore(new MongoClient(host + ":" + port), "explorviz");
    this.datastore.ensureIndexes();
  }
  
  @Override
  public Datastore provide() {
    return this.datastore;
  }

  @Override
  public void dispose(Datastore instance) {
    // TODO Auto-generated method stub
  }
  
  

}
