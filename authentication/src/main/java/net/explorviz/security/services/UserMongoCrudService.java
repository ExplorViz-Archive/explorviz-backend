package net.explorviz.security.services;

import com.mongodb.MongoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.explorviz.security.util.CountingIdGenerator;
import net.explorviz.security.util.IdGenerator;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.morphia.Datastore;

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
public class UserMongoCrudService implements MongoCrudService<User> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserMongoCrudService.class);

  private final IdGenerator<Long> idGen;

  private final Datastore datastore;



  /**
   * Creates a new {@code UserCrudMongoDb}.
   *
   */
  @Inject
  public UserMongoCrudService(final Datastore datastore) {

    this.datastore = datastore;

    final User userWithMaxId = this.datastore.createQuery(User.class).order("id").get();

    // Create a new id generator, which will count upwards beginning from the max id
    long counterInitValue = 0L;

    if (userWithMaxId != null) {
      counterInitValue = userWithMaxId.getId();
    }

    this.idGen = new CountingIdGenerator(counterInitValue);
  }


  @Override
  public List<User> getAll() {
    return this.datastore.createQuery(User.class).asList();
  }

  @Override
  public Optional<User> saveNewEntity(final User user) throws MongoException {
    // Generate an id
    user.setId(this.idGen.next());



    this.datastore.save(user);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Inserted new user with id " + user.getId());
    }
    return Optional.ofNullable(user);
  }

  @Override
  public void updateEntity(final User user) throws MongoException {
    this.datastore.save(user);
  }

  @Override
  public Optional<User> getEntityById(final Long id) throws MongoException {

    final User userObject = this.datastore.get(User.class, id);
    System.out.println(userObject.getSettings());

    return Optional.ofNullable(userObject);
  }

  @Override
  public void deleteEntityById(final Long id) throws MongoException {

    this.datastore.delete(User.class, id);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Deleted user with id " + id);
    }

  }


  @Override
  public Optional<User> findEntityByFieldValue(final String field, final Object value) {

    final User foundUser = this.datastore.createQuery(User.class).filter(field, value).get();

    return Optional.ofNullable(foundUser);
  }

  /**
   * Retrieves a list of all users, that have a specific role assigned.
   *
   * @param roleName role to search for
   * @return a list of all users, that have the given role assigned
   */
  public List<User> getUsersByRole(final String roleName) throws MongoException {

    // TODO find smarter (MongoDB based) way to check for roles

    final Role role = this.datastore.createQuery(Role.class).filter("descriptor", roleName).get();

    final List<User> listToBeReturned = new ArrayList<>();

    List<User> userList = this.datastore.createQuery(User.class).asList();



    userList =
        userList.stream()
            .filter(u -> u.getRoles().stream()
                .anyMatch(r -> r.getId().longValue() == role.getId().longValue()
                    && r.getDescriptor().equals(role.getDescriptor())))
            .collect(Collectors.toList());

    return userList;
  }



}
