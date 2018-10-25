package net.explorviz.security.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import net.explorviz.security.persistence.mongo.MongoAdapter;
import net.explorviz.security.persistence.mongo.MongoClientHelper;
import net.explorviz.security.persistence.mongo.UserAdapter;
import net.explorviz.shared.security.User;
import net.explorviz.shared.server.helper.PropertyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Offers CRUD operations on user objects, backed by a MongoDB instance as persistence layer.
 *
 */
public class UserCrudMongoDb implements UserCrudService {



  private static final Logger LOGGER =
      LoggerFactory.getLogger(UserCrudMongoDb.class.getSimpleName());

  private final DBCollection userCollection;

  /**
   * Creates a new {@code UserCrudMongoDb}
   */
  public UserCrudMongoDb() {
    final String dbname = PropertyHelper.getStringProperty("mongo.db");
    final MongoClient client = MongoClientHelper.getInstance().getMongoClient();
    this.userCollection = client.getDB(dbname).getCollection("user");
  }


  @Override
  public User saveNewUser(final User user) {
    final MongoAdapter<User> userAdapter = new UserAdapter();
    final DBObject userDBObject = userAdapter.toDBObject(user);

    try {
      final WriteResult result = this.userCollection.insert(userDBObject);

      final long id = Long.parseLong(userDBObject.get("_id").toString(), 16);

      LOGGER.info("Inserted new user with id " + id);

      return new User(id, user.getUsername(), user.getPassword(), user.getRoles());

    } catch (final MongoException ex) {
      LOGGER.error("Could not insert new user: " + ex.getMessage() + " (" + ex.getCode() + ")");
      throw new InternalServerErrorException();
    }
  }

  @Override
  public void updateUser(final User user) {
    final DBObject query = new BasicDBObject("_id", user.getId());
    final MongoAdapter<User> userAdapter = new UserAdapter();
    final DBObject userDBObject = userAdapter.toDBObject(user);

    try {
      final WriteResult result = this.userCollection.update(query, userDBObject);
    } catch (final MongoException ex) {
      LOGGER.error("Could not update user: " + ex.getMessage() + " (" + ex.getCode() + ")");
      throw new InternalServerErrorException();
    }
  }

  @Override
  public User getUserById(final Long id) {
    final MongoAdapter<User> userAdapter = new UserAdapter();
    try {
      final DBObject userObject = this.userCollection.findOne(id);

      if (userObject == null) {
        LOGGER.info("Could not find user with id " + id);
        throw new NotFoundException();
      }

      return userAdapter.fromDBObject(userObject);

    } catch (final MongoException ex) {
      LOGGER.error("Could not update user: " + ex.getMessage() + " (" + ex.getCode() + ")");
      throw new InternalServerErrorException();
    }

  }

  @Override
  public List<User> getUsersByRole(final String role) {
    final DBObject query = new BasicDBObject("roles", role);
    final MongoAdapter<User> userAdapter = new UserAdapter();
    try {
      final DBCursor userObjects = this.userCollection.find(query);

      final List<User> users = userObjects.toArray().stream().map(o -> userAdapter.fromDBObject(o))
          .collect(Collectors.toList());

      return users;

    } catch (final MongoException ex) {
      LOGGER.error("Could not update user: " + ex.getMessage() + " (" + ex.getCode() + ")");
      throw new InternalServerErrorException();
    }

  }

  @Override
  public void deleteUserById(final Long id) {
    try {
      final DBObject query = new BasicDBObject("_id", id);
      this.userCollection.remove(query);

      LOGGER.info("Deleted user with id " + id);

    } catch (final MongoException ex) {
      LOGGER.error("Could not update user: " + ex.getMessage() + " (" + ex.getCode() + ")");
      throw new InternalServerErrorException();
    }

  }

}
