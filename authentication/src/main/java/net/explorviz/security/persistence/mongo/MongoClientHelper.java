package net.explorviz.security.persistence.mongo;

import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import javax.ws.rs.InternalServerErrorException;
import net.explorviz.shared.server.helper.PropertyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the access to the {@link MongoClient} instance. Only one instance of {@link MongoClient}
 * should exist in the application for performance reasons (pooling).
 *
 */
public final class MongoClientHelper {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName());


  private static final String DEFAUL_ADDRESS = "localhost";
  private static final int DEFAULT_PORT = 27017;

  private static MongoClientHelper instance;

  public static MongoClientHelper getInstance() {
    if (instance == null) {
      instance = new MongoClientHelper();
    }

    return instance;
  }

  private final MongoClient client;

  /**
   * Retrieves the connection to the database (i.e. the {@link MongoClient}). If the client is
   * closed, all connections are lost and the mongoDB instance cannot longer be used.
   *
   * @return
   */
  public MongoClient getMongoClient() {
    return this.client;
  }


  private MongoClientHelper() {
    final String addr = PropertyHelper.getStringProperty("mongo.ip") != null ? DEFAUL_ADDRESS
        : PropertyHelper.getStringProperty("mongo.ip");
    final String port = PropertyHelper.getStringProperty("mongo.ip") != null ? DEFAUL_ADDRESS
        : PropertyHelper.getStringProperty("mongo.port");

    try {
      this.client = new MongoClient("mongodb://" + addr + ":" + port);
    } catch (final UnknownHostException e) {
      this.LOGGER.error("Target not reachable: " + addr + ":" + port);
      throw new InternalServerErrorException();
    }

  }

}
