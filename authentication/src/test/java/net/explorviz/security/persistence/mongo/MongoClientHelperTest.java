package net.explorviz.security.persistence.mongo;

import com.mongodb.MongoClient;
import org.junit.After;
import org.junit.Test;

public class MongoClientHelperTest {


  @After
  public void tearDown() {
    MongoClientHelper.reset();
  }

  @Test
  public void testConnection() {
    // No exception == Good
    final MongoClient client = MongoClientHelper.getInstance().getMongoClient();
  }

}
