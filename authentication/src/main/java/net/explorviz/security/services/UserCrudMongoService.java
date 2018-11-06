package net.explorviz.security.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.explorviz.security.persistence.mongo.MongoAdapter;
import net.explorviz.security.persistence.mongo.MongoClientHelper;
import net.explorviz.security.persistence.mongo.UserAdapter;
import net.explorviz.security.util.CountingIdGenerator;
import net.explorviz.security.util.IdGenerator;
import net.explorviz.shared.security.User;
import net.explorviz.shared.server.helper.PropertyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Offers CRUD operations on user objects, backed by a MongoDB instance as persistence layer.
 *
 * Each user has the following fields:
 * <ul>
 * <li>id: the unique id of the user</li>
 * <li>username: name of the user, unique</li>
 * <li>password: hashed password</li>
 * <li>roles: list of role that are assigned to the user</li>
 * </ul>
 *
 */
public class UserCrudMongoService implements UserCrudService {


  private final IdGenerator<Long> idGen;

  private static final Logger LOGGER =
      LoggerFactory.getLogger(UserCrudMongoService.class.getSimpleName());

  private final DBCollection userCollection;

  /**
   * Creates a new {@code UserCrudMongoDb}
   */
  public UserCrudMongoService() {
    final String dbname = PropertyHelper.getStringProperty("mongo.db");
    final MongoClient client = MongoClientHelper.getInstance().getMongoClient();
    this.userCollection = client.getDB(dbname).getCollection("user");


    // Create a new id generator, that will count from the max id upwards

    // Find all ids
    final DBCursor maxCursor =
        this.userCollection.find(new BasicDBObject(), new BasicDBObject("id", 1))
            .sort(new BasicDBObject("id", -1)).limit(1);
    Long maxId = 0L;

    // If there are objects in the db, find the maximum id
    if (maxCursor.hasNext()) {
      final DBObject o = maxCursor.next();
      maxId = o.get("id") == null ? 0L : (Long) o.get("id");
    }

    this.idGen = new CountingIdGenerator(maxId);
    LOGGER.info(this.idGen.toString());

    // Create indices with unique constraints
    this.userCollection.createIndex(new BasicDBObject("username", 1),
        new BasicDBObject("unique", true));
    this.userCollection.createIndex(new BasicDBObject("id", 1), new BasicDBObject("unique", true));
  }


  @Override
  public List<User> getAll() {

    final MongoAdapter<User> adapter = new UserAdapter();
    return this.userCollection.find().toArray().stream().map(o -> adapter.fromDBObject(o))
        .collect(Collectors.toList());

  }

  @Override
  public Optional<User> saveNewUser(final User user) throws MongoException {
    // Generate an id
    user.setId(this.idGen.next());
    final MongoAdapter<User> userAdapter = new UserAdapter();
    final DBObject userDBObject = userAdapter.toDBObject(user);


    final WriteResult result = this.userCollection.insert(userDBObject);

    final long id = (Long) userDBObject.get("id");

    LOGGER.info("Inserted new user with id " + id);
    return Optional
        .ofNullable(new User(id, user.getUsername(), user.getPassword(), user.getRoles()));

  }

  @Override
  public void updateUser(final User user) throws MongoException {
    final DBObject query = new BasicDBObject("id", user.getId());
    final MongoAdapter<User> userAdapter = new UserAdapter();
    final DBObject userDBObject = userAdapter.toDBObject(user);


    this.userCollection.update(query, userDBObject);

  }

  @Override
  public Optional<User> getUserById(final Long id) throws MongoException {
    final MongoAdapter<User> userAdapter = new UserAdapter();

    final DBObject userObject = this.userCollection.findOne(new BasicDBObject("id", id));


    return Optional.ofNullable(userAdapter.fromDBObject(userObject));

  }

  @Override
  public List<User> getUsersByRole(final String role) throws MongoException {
    final DBObject query = new BasicDBObject("roles", role);
    final MongoAdapter<User> userAdapter = new UserAdapter();

    final DBCursor userObjects = this.userCollection.find(query);

    final List<User> users = userObjects.toArray().stream().map(o -> userAdapter.fromDBObject(o))
        .collect(Collectors.toList());

    return users;

  }

  @Override
  public void deleteUserById(final Long id) throws MongoException {
    final DBObject query = new BasicDBObject("id", id);
    this.userCollection.remove(query);

    LOGGER.info("Deleted user with id " + id);
  }


  @Override
  public Optional<User> findUserByName(final String username) {
    final MongoAdapter<User> userAdapter = new UserAdapter();

    final DBObject query = new BasicDBObject("username", username);
    final DBObject foundUser = this.userCollection.findOne(query);

    return Optional.ofNullable(userAdapter.fromDBObject(foundUser));

  }



}