package net.explorviz.security.server.injection;

import com.mongodb.MongoClient;
import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import org.glassfish.hk2.api.Factory;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;

public class DatastoreFactory implements Factory<Datastore> {

  // @Config("mongo.port")
  // private String port;

  private final Datastore datastore;

  @Config("mongo.ip")
  @Config("mongo.port")
  public DatastoreFactory(final String host, final String port) {

    final Morphia morphia = new Morphia();

    // Map the model classes
    morphia.map(User.class, Role.class);

    this.datastore = morphia.createDatastore(new MongoClient(host + ":" + port), "explorviz");
    this.datastore.ensureIndexes();
  }

  @Override
  public Datastore provide() {
    return this.datastore;
  }

  @Override
  public void dispose(final Datastore instance) {
    // nothing to do
  }

}
