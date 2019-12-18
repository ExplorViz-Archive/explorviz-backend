package net.explorviz.security.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.inject.Inject;
import net.explorviz.security.services.exceptions.DuplicateUserException;
import net.explorviz.security.services.exceptions.UserCrudException;
import net.explorviz.security.user.User;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.querying.Query;
import net.explorviz.shared.querying.QueryResult;
import net.explorviz.shared.querying.Queryable;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.morphia.Datastore;
import xyz.morphia.query.FindOptions;

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
public class UserService implements Queryable<User> {

  private static final String ADMIN = "admin";

  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);



  private final Datastore datastore;


  private final IdGenerator idGenerator;

  private final KafkaUserService kafkaService;

  /**
   * Creates a new UserMongoDB.
   *
   * @param datastore - the datastore instance
   */
  @Inject
  public UserService(final Datastore datastore, final IdGenerator idGenerator,
      final KafkaUserService kafkaService) {
    this.idGenerator = idGenerator;
    this.datastore = datastore;
    this.kafkaService = kafkaService;
  }


  public List<User> getAll() {
    return this.datastore.createQuery(User.class).asList();
  }


  /**
   * Persists an user entity.
   *
   * @param user - a user entity
   * @return an Optional, which contains a User or is empty
   * @throws UserCrudException if the user could not be saved
   */
  public User saveNewEntity(final User user) throws UserCrudException {
    // Generate an id
    user.setId(this.idGenerator.generateId());

    try {
      this.datastore.save(user);

      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Inserted new user with id " + user.getId());
      }
    } catch (final DuplicateKeyException e) {
      throw new DuplicateUserException(
          String.format("User with %s already exists", user.getUsername()));
    } catch (final MongoException e) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error("Could not save user: " + e.getMessage());
      }
      throw new UserCrudException("Could no save user", e);
    }

    try {
      this.kafkaService.publishCreated(user.getId());
    } catch (final JsonProcessingException e) {
      LOGGER.warn("New user no published to kafka", e);
    }
    return user;
  }

  public void updateEntity(final User user) {
    this.datastore.save(user);
  }

  /**
   * Returns the user with the given id.
   *
   * @param id the id of the user to retrieve
   * @return and {@link Optional} which contains the user with given id or is empty if such a user
   *         does not exist
   */
  public Optional<User> getEntityById(final String id) {

    final User userObject = this.datastore.get(User.class, id);

    return Optional.ofNullable(userObject);
  }

  /**
   * Tries to delete the user with the given id. If such a user does not exists, nothing happens.
   *
   * @param id id of the user to delete
   * @throws UserCrudException if the id belongs to the a user with the admin role and there are no
   *         other admin users. This prevents the deletion of the last admin.
   */
  public void deleteEntityById(final String id) throws UserCrudException {

    if (this.isLastAdmin(id)) {
      throw new UserCrudException("Can not delete last admin");
    }

    final WriteResult w = this.datastore.delete(User.class, id);

    if (w.getN() > 0) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Deleted user with id " + id);
      }
      try {
        this.kafkaService.publishDeleted(id);
      } catch (final JsonProcessingException e) {
        LOGGER.warn("User no published", e);
      }
    }


  }

  /**
   * Helper method to check whether the user with the given id is the only admin.
   *
   * @param id user id
   * @return {@code true} iff the user with the given id has the role "admin" and there is no other
   *         user with this role.
   */
  private boolean isLastAdmin(final String id) {
    User user;
    try {
      user = this.getEntityById(id).get();
    } catch (final NoSuchElementException e) {
      return false;
    }

    final boolean isadmin = user.getRoles().stream().filter(r -> r.equals(ADMIN)).count() == 1;

    final boolean otheradmin = this.getAll()
        .stream()
        .filter(u -> new ArrayList<>(u.getRoles()).contains(ADMIN))
        .anyMatch(u -> !u.getId().equals(id));


    return isadmin && !otheradmin;

  }


  /**
   * Find the first user that satisfies the condition specified in the parameters.
   *
   * @param field the field to compare
   * @param value the value to compare with
   * @return the first user which's field contains the given value.
   */
  public Optional<User> findEntityByFieldValue(final String field, final Object value) {

    final User foundUser = this.datastore.createQuery(User.class).filter(field, value).get();

    return Optional.ofNullable(foundUser);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public QueryResult<User> query(final Query<User> query) {
    final String roleField = "roles";
    final xyz.morphia.query.Query<User> q = this.datastore.createQuery(User.class);


    if (query.doFilter()) {
      final List<String> roles = query.getFilters().get(roleField);
      final List<String> batchIds = query.getFilters().get("batchid");

      // Filter by roles
      if (roles != null) {
        q.field(roleField).hasAllOf(new ArrayList<>(roles));
      }

      // Filter by batch id, if more than one is give, ignore all but the first
      if (batchIds != null) {
        q.field("batchId").equal(batchIds.get(0));
      }

    }



    final FindOptions options = new FindOptions();

    if (query.doPaginate()) {
      options.limit(query.getPageSize());
      options.skip(query.getPageNumber() * query.getPageSize());
    }

    final long total = q.count();
    final List<User> ret = q.asList(options);

    return new QueryResult<>(query, ret, (int) total);
  }



}
