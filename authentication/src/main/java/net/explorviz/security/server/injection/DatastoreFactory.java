package net.explorviz.security.server.injection;

import com.mongodb.MongoClient;
import net.explorviz.shared.annotations.Config;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import org.glassfish.hk2.api.Factory;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;

public class DatastoreFactory implements Factory<Datastore> {

  @Config("mongo.ip")
  private String host;

  @Config("mongo.port")
  private String port;

  private final Datastore datastore;

  public DatastoreFactory() {

    System.out.println("ip " + this.host);
    System.out.println("port " + this.port);

    System.out.println("ip " + this.host);
    System.out.println("port " + this.port);

    final Morphia morphia = new Morphia();

    // Map the model classes
    morphia.map(User.class, Role.class);

    this.datastore = morphia.createDatastore(new MongoClient("127.0.0.1:27017"), "explorviz");

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
