package net.explorviz.security.persistence.mongo;

import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import javax.ws.rs.InternalServerErrorException;
import net.explorviz.shared.annotations.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the access to the {@link MongoClient} instance. Only one instance of {@link MongoClient}
 * should exist in the application for performance reasons (pooling).
 *
 */
public final class MongoClientHelper {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName());


  private static final String DEFAUL_ADDRESS = "192.168.99.100";
  private static final String DEFAULT_PORT = "27017";



  @Config("mongo.ip")
  private String host;

  @Config("mongo.port")
  private String port;



  private MongoClient client = null;

  /**
   * Retrieves the connection to the database (i.e. the {@link MongoClient}). If the client is
   * closed, all connections are lost and the mongoDB instance cannot longer be used. Use
   * {@link MongoClientHelper#reset()} to obtain a new client.
   *
   * @return the client
   */
  public MongoClient getMongoClient() {
    if (this.client == null) {
      this.LOGGER.info("Connecting to " + this.host + ":" + this.port);

      try {
        this.client = new MongoClient(this.host + ":" + this.port);
        this.LOGGER.info("Connected to " + this.host + ":" + this.port);
      } catch (final UnknownHostException e) {
        this.LOGGER.error("Target not reachable: " + this.host + ":" + this.port);
        throw new InternalServerErrorException();
      }
    }

    return this.client;
  }

}
