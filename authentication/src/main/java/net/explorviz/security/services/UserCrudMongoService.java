package net.explorviz.security.services;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import net.explorviz.security.persistence.mongo.FieldHelper;
import net.explorviz.security.persistence.mongo.MongoClientHelper;
import net.explorviz.security.util.CountingIdGenerator;
import net.explorviz.security.util.IdGenerator;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
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



  private final MongoCollection<User> userCollection;
  private final MongoCollection<Role> roleCollection;



  /**
   * Creates a new {@code UserCrudMongoDb}.
   *
   */
  @Inject
  public UserCrudMongoService(final MongoClientHelper mongoHelper) {

    this.roleCollection =
        mongoHelper.getMongoClient().getDatabase(DBNAME).getCollection("role", Role.class);

    this.roleCollection.insertOne(new Role(3L, "admin"));

    this.userCollection =
        mongoHelper.getMongoClient().getDatabase(DBNAME).getCollection("user", User.class);


    // Create a new id generator, which will count upwards beginning from the max id

    final User userWithMaxId =
        this.userCollection.find().sort(new BasicDBObject("id", -1)).limit(1).first();

    long counterInitValue = 0L;

    if (userWithMaxId != null) {
      counterInitValue = userWithMaxId.getId();
    }

    this.idGen = new CountingIdGenerator(counterInitValue);

    // Create indices with unique constraints (if not existing)
    this.userCollection.createIndex(new BasicDBObject(FieldHelper.FIELD_USERNAME, 1),
        new IndexOptions().unique(true));
    this.userCollection.createIndex(new BasicDBObject(FieldHelper.FIELD_ID, 1),
        new IndexOptions().unique(true));
  }


  @Override
  public List<User> getAll() {

    // final MongoAdapter<User> adapter = new UserAdapter();
    // return this.userCollection.find().toArray().stream().map(o -> adapter.fromDbObject(o))
    // .collect(Collectors.toList());
    return new ArrayList<>();

  }

  @Override
  public Optional<User> saveNewUser(final User user) throws MongoException {
    // Generate an id

    user.setId(this.idGen.next());

    final Role adminRole = this.roleCollection.find().limit(1).first();

    System.out.println(adminRole.getDescriptor());

    this.roleCollection.updateOne(eq("descriptor", "admin"), set("descriptor", "test"));

    // user.setRoles(Arrays.asList(adminRole));

    this.userCollection.insertOne(user);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Inserted new user with id " + user.getId());
    }
    return Optional.ofNullable(
        new User(user.getId(), user.getUsername(), user.getPassword(), user.getRoles()));
  }

  @Override
  public void updateUser(final User user) throws MongoException {
    // final DBObject query = new BasicDBObject(FieldHelper.FIELD_ID, user.getId());
    // final MongoAdapter<User> userAdapter = new UserAdapter();
    // final DBObject userDbObject = userAdapter.toDbObject(user);


    // this.userCollection.update(query, userDbObject);

  }

  @Override
  public Optional<User> getUserById(final Long id) throws MongoException {

    /*
     * final DBObject userObject = this.userCollection.findOne(new BasicDBObject("id", id));
     *
     * if (userObject == null) { return Optional.empty(); }
     *
     * final MongoAdapter<User> userAdapter = new UserAdapter(); return
     * Optional.ofNullable(userAdapter.fromDbObject(userObject));
     */
    return Optional.empty();

  }

  @Override
  public List<User> getUsersByRole(final String role) throws MongoException {
    /*
     * final DBObject query = new BasicDBObject("roles", role); final MongoAdapter<User> userAdapter
     * = new UserAdapter();
     *
     * final DBCursor userObjects = this.userCollection.find(query);
     *
     * return userObjects.toArray().stream().map(o -> userAdapter.fromDbObject(o))
     * .collect(Collectors.toList());
     */
    return new ArrayList<>();
  }

  @Override
  public void deleteUserById(final Long id) throws MongoException {
    /*
     * final DBObject query = new BasicDBObject("id", id); this.userCollection.remove(query);
     *
     * if (LOGGER.isInfoEnabled()) { LOGGER.info("Deleted user with id " + id); }
     */
  }


  @Override
  public Optional<User> findUserByName(final String username) {
    /*
     * final DBObject query = new BasicDBObject("username", username); final DBObject foundUser =
     * this.userCollection.findOne(query);
     *
     * if (foundUser == null) { return Optional.empty(); }
     *
     * final MongoAdapter<User> userAdapter = new UserAdapter(); return
     * Optional.ofNullable(userAdapter.fromDbObject(foundUser));
     */
    return Optional.empty();
  }



}
