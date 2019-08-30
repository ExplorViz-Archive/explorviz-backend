package net.explorviz.security.server.helper;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

public class MongoHelper {

  /*
   * Better: Use properties. But the properties are override by environment variables and the
   * PropertyHelper currently is not capable of accessing them. @Config is not possible since no
   * DI is used for integrataion tests
   */
  public static final String USER_MONGO_HOST = "localhost:27017";

  private static String DB_NAME = "explorviz";



  public enum UserCollections {
    USERS("users"), ROLES("roles");

    String name;

    private UserCollections(String name) {
      this.name = name;
    }
  }

  MongoClient client;

  public MongoHelper(String host) {
    this.client = new MongoClient(host);
  }

  public void emptyCollection(String collection) {
    client.getDatabase(DB_NAME).getCollection(collection).deleteMany(new BasicDBObject());
  }

  public void dropDB(){
    client.getDatabase(DB_NAME).drop();
  }


  public void close(){
    client.close();
  }

}
