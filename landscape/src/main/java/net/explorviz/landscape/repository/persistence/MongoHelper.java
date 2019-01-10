package net.explorviz.landscape.repository.persistence;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import net.explorviz.shared.annotations.Config;

public class MongoHelper {

  private static final String LANDSCAPE_COLLECTION = "landscape";
  private static MongoClient client = null;

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
    if (client == null) {
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
    return this.getClient().getDB(this.dbName);
  }

  /**
   * Returns a connection to the landscape collection.
   */
  public DBCollection getLandscapeCollection() {
    return this.getDatabase().getCollection(LANDSCAPE_COLLECTION);
  }

  private String getUri() {
    return this.host + ':' + this.port;
  }

  /**
   * Closes all resources associated with this instance, in particular any open network connections.
   * Once called, this instance and any databases obtained from it can no longer be used.
   */
  public void close() {
    this.getClient().close();
  }


}
