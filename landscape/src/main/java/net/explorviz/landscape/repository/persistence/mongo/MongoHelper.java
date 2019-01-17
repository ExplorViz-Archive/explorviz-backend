package net.explorviz.landscape.repository.persistence.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import net.explorviz.shared.annotations.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MongoHelper { // NOPMD

  private static final String DEFAULT_HOST = "localhost";
  private static final String DEFAULT_PORT = "27018";
  private static final String DEFAULT_DB = "explorviz";
  private static final String LANDSCAPE_COLLECTION = "landscape";
  private static final String REPLAY_COLLECTION = "replay";

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoHelper.class.getSimpleName());


  private static MongoClient client; // NOPMD



  @Config("mongo.host")
  private String host;

  @Config("mongo.port")
  private String port;

  @Config("mongo.db")
  private String dbName;



  /*
   * From mongo documentation: "It is important to limit the number of MongoClient instances in your
   * application, hence why we suggest a singleton - the MongoClient is effectively the connection
   * pool, so for every new MongoClient, you are opening a new pool."
   */

  private MongoHelper() {
    if (MongoHelper.client == null) {
      MongoHelper.client = new MongoClient(new MongoClientURI(this.getUri()));
    }
  }

  /**
   * Returns a {@link MongoClient} which is connected to the database given in the configuration
   * file. This method is private in order to prevent unintentional closing of alls connections.
   *
   * @see #close()
   */
  private MongoClient getClient() {
    return MongoHelper.client;
  }

  /**
   * Returns the {@link MongoDatabase} given in the configuration file.
   *
   */
  public DB getDatabase() {

    String dbName = this.dbName;

    if (dbName == null || dbName.isEmpty()) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn("No database name given, falling back to " + DEFAULT_DB);
      }
      dbName = DEFAULT_DB;
    }

    return this.getClient().getDB(dbName);
  }

  /**
   * Returns a connection to the landscape collection.
   */
  public DBCollection getLandscapeCollection() {
    return this.getDatabase().getCollection(LANDSCAPE_COLLECTION);
  }

  public DBCollection getReplayCollection() {
    return this.getDatabase().getCollection(REPLAY_COLLECTION);
  }


  private String getUri() {

    String host = this.host;
    String port = this.port;

    if (host == null || host.isEmpty()) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn("No host configured, falling back to " + DEFAULT_HOST);
      }
      host = DEFAULT_HOST;
    }

    if (port == null || port.isEmpty()) {
      port = DEFAULT_PORT;
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn("No port configured, falling back to " + DEFAULT_PORT);
      }
    }

    return "mongodb://" + host + ':' + port;
  }

  /**
   * Closes all resources associated with this instance, in particular any open network connections.
   * Once called, this instance and any databases obtained from it can no longer be used.
   */
  public void close() {
    this.getClient().close();
  }


}
