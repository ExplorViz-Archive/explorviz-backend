package net.explorviz.security.server.injection;

import com.mongodb.MongoClient;
import net.explorviz.security.user.User;
import net.explorviz.shared.config.annotations.Config;
import org.glassfish.hk2.api.Factory;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;

/**
 * Handles the creation of {@link Datastore} for DI.
 *
 */
public class DatastoreFactory implements Factory<Datastore> {

  // @Config("mongo.port")
  // private String port;

  private final Datastore datastore;

  /**
   * Creates new Datastore, which will be used for injection.
   *
   * @param host the mongo db host
   * @param port the port
   */
  @Config("mongo.host")
  @Config("mongo.port")
  public DatastoreFactory(final String host, final String port) {

    final Morphia morphia = new Morphia();

    // Map the model classes
    morphia.map(User.class);

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
