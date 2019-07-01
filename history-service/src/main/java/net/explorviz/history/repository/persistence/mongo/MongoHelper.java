package net.explorviz.history.repository.persistence.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.config.annotations.ConfigValues;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Helper class providing methods for mongoDB persistence.
 */
public final class MongoHelper { // NOPMD

  public static final String FIELD_LANDSCAPE = "landscape"; // NOCS
  public static final String FIELD_ID = "_id";
  public static final String FIELD_TIMESTAMP = "timestamp";
  public static final String FIELD_REQUESTS = "totalRequests";

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoHelper.class);

  private static final String DEFAULT_HOST = "localhost";
  private static final String DEFAULT_PORT = "27018";
  private static final String DEFAULT_DB = "explorviz";
  private static final String LANDSCAPE_COLLECTION = "landscape";
  private static final String REPLAY_COLLECTION = "replay";

  private MongoClient client;

  private final String host;

  private final String port;

  private final String dbName;

  /*
   * From mongo documentation: "It is important to limit the number of MongoClient instances in your
   * application, hence why we suggest a singleton - the MongoClient is effectively the connection
   * pool, so for every new MongoClient, you are opening a new pool."
   */
  /**
   * Creates a new MongoHelper, which should only be performed by DI.
   *
   * @throws IllegalStateException If an instance already exists.
   */
  @ConfigValues({@Config("mongo.host"), @Config("mongo.port"), @Config("mongo.db")})
  public MongoHelper(final String host, final String port, final String dbName) {

    this.host = host;
    this.port = port;
    this.dbName = dbName;

    if (this.client == null) {
      this.client = new MongoClient(new MongoClientURI(this.getUri()));
    } else {
      throw new IllegalStateException("Onl y one instance allowed");
    }
  }

  /**
   * Returns a {@link MongoClient} which is connected to the database given in the configuration
   * file. This method is private in order to prevent unintentional closing of all connections.
   *
   * @see #close()
   */
  private MongoClient getClient() {
    return this.client;
  }

  /**
   * Returns the {@link MongoDatabase} given in the configuration file.
   *
   */
  public MongoDatabase getDatabase() {

    String dbName = this.dbName;

    if (dbName == null || dbName.isEmpty()) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn("No database name given, falling back to " + DEFAULT_DB);
      }
      dbName = DEFAULT_DB;
    }

    return this.getClient().getDatabase(dbName);
  }

  /**
   * Returns a connection to the landscape collection.
   */
  public MongoCollection<Document> getLandscapeCollection() {
    return this.getDatabase().getCollection(LANDSCAPE_COLLECTION);
  }

  public MongoCollection<Document> getReplayCollection() {
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
