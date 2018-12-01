package net.explorviz.security.persistence.mongo;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoTimeoutException;
import javax.ws.rs.InternalServerErrorException;
import net.explorviz.shared.annotations.Config;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;

/**
 * Handles the access to the {@link MongoClient} instance. Only one instance of {@link MongoClient}
 * should exist in the application for performance reasons (pooling).
 *
 */
public final class MongoClientHelper {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MongoClientHelper.class.getSimpleName());


  @Config("mongo.ip")
  private String host;

  @Config("mongo.port")
  private String port;



  private MongoClient client;

  /**
   * Retrieves the connection to the database (i.e. the {@link MongoClient}). If the client is
   * closed, all connections are lost and the mongoDB instance cannot longer be used. Use
   * {@link MongoClientHelper#reset()} to obtain a new client.
   *
   * @return the client
   */
  public MongoClient getMongoClient() {
    if (this.client == null) {
      try {
        final CodecRegistry pojoCodecRegistry =
            fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        final Morphia morphia = new Morphia();

        morphia.map(User.class, Role.class);

        final Datastore datastore =
            morphia.createDatastore(new MongoClient(this.host + ":" + this.port), "explorviz");



        this.client = new MongoClient(this.host + ":" + this.port,
            MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
        this.client.getDatabase("explorviz");
        if (LOGGER.isInfoEnabled()) {
          LOGGER.info("Connected to " + this.host + ":" + this.port);
        }
      } catch (final MongoTimeoutException e) {
        if (LOGGER.isErrorEnabled()) {
          LOGGER.error("Target not reachable: " + this.host + ":" + this.port);
        }
        throw new InternalServerErrorException(e);
      }
    }

    return this.client;
  }

}
