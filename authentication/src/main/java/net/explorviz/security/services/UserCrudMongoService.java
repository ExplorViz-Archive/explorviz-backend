package net.explorviz.security.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.explorviz.security.persistence.mongo.FieldHelper;
import net.explorviz.security.persistence.mongo.MongoAdapter;
import net.explorviz.security.persistence.mongo.MongoClientHelper;
import net.explorviz.security.persistence.mongo.UserAdapter;
import net.explorviz.security.util.CountingIdGenerator;
import net.explorviz.security.util.IdGenerator;
import net.explorviz.shared.security.User;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Offers CRUD operations on user objects, backed by a MongoDB instance as persistence layer. Each
 * user has the following fields:
 * <ul>
 * <li>id: the unique id of the user</li>
 * <li>username: name of the user, unique</li>
 * <li>password: hashed password</li>
 * <li>roles: list of role that are assigned to the user</li>
 * </ul>
 *
 */
@Service
public class UserCrudMongoService implements UserCrudService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(UserCrudMongoService.class.getSimpleName());

  private static final String DBNAME = "explorviz";

  private static final String UNIQUE = "unique";


  private final IdGenerator<Long> idGen;



  private final DBCollection userCollection;



  /**
   * Creates a new {@code UserCrudMongoDb}.
   *
   */
  @Inject
  public UserCrudMongoService(final MongoClientHelper mongoHelper) {

    this.userCollection = mongoHelper.getMongoClient().getDB(DBNAME).getCollection("user");


    /* Create a new id generator, which will count upwards beginning from the max id */

    // Find all ids
    final DBCursor maxCursor =
        this.userCollection.find(new BasicDBObject(), new BasicDBObject(FieldHelper.FIELD_ID, 1))
            .sort(new BasicDBObject(FieldHelper.FIELD_ID, -1)).limit(1);
    Long maxId = 0L;

    // If there are objects in the db, find the maximum id
    if (maxCursor.hasNext()) {
      final DBObject o = maxCursor.next();
      maxId = o.get("id") == null ? 0L : (Long) o.get(FieldHelper.FIELD_ID);
    }

    this.idGen = new CountingIdGenerator(maxId);


    // Create indices with unique constraints (if not existing)
    this.userCollection.createIndex(new BasicDBObject(FieldHelper.FIELD_USERNAME, 1),
        new BasicDBObject(UNIQUE, true));
    this.userCollection.createIndex(new BasicDBObject(FieldHelper.FIELD_ID, 1),
        new BasicDBObject(UNIQUE, true));
  }


  @Override
  public List<User> getAll() {

    final MongoAdapter<User> adapter = new UserAdapter();
    return this.userCollection.find().toArray().stream().map(o -> adapter.fromDbObject(o))
        .collect(Collectors.toList());

  }

  @Override
  public Optional<User> saveNewUser(final User user) throws MongoException {
    // Generate an id
    user.setId(this.idGen.next());
    final MongoAdapter<User> userAdapter = new UserAdapter();
    final DBObject userDbObject = userAdapter.toDbObject(user);

    final long id = (Long) userDbObject.get(FieldHelper.FIELD_ID);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Inserted new user with id " + id);
    }
    return Optional
        .ofNullable(new User(id, user.getUsername(), user.getPassword(), user.getRoles()));

  }

  @Override
  public void updateUser(final User user) throws MongoException {
    final DBObject query = new BasicDBObject("id", user.getId());
    final MongoAdapter<User> userAdapter = new UserAdapter();
    final DBObject userDbObject = userAdapter.toDbObject(user);


    this.userCollection.update(query, userDbObject);

  }

  @Override
  public Optional<User> getUserById(final Long id) throws MongoException {

    final DBObject userObject = this.userCollection.findOne(new BasicDBObject("id", id));

    if (userObject == null) {
      return Optional.empty();
    }

    final MongoAdapter<User> userAdapter = new UserAdapter();
    return Optional.ofNullable(userAdapter.fromDbObject(userObject));

  }

  @Override
  public List<User> getUsersByRole(final String role) throws MongoException {
    final DBObject query = new BasicDBObject("roles", role);
    final MongoAdapter<User> userAdapter = new UserAdapter();

    final DBCursor userObjects = this.userCollection.find(query);

    return userObjects.toArray().stream().map(o -> userAdapter.fromDbObject(o))
        .collect(Collectors.toList());

  }

  @Override
  public void deleteUserById(final Long id) throws MongoException {
    final DBObject query = new BasicDBObject("id", id);
    this.userCollection.remove(query);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Deleted user with id " + id);
    }
  }


  @Override
  public Optional<User> findUserByName(final String username) {
    final DBObject query = new BasicDBObject("username", username);
    final DBObject foundUser = this.userCollection.findOne(query);

    if (foundUser == null) {
      return Optional.empty();
    }

    final MongoAdapter<User> userAdapter = new UserAdapter();
    return Optional.ofNullable(userAdapter.fromDbObject(foundUser));

  }



}
