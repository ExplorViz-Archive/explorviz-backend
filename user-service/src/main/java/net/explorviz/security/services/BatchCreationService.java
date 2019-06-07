package net.explorviz.security.services;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.explorviz.security.model.UserBatchRequest;
import net.explorviz.security.util.PasswordStorage;
import net.explorviz.security.util.PasswordStorage.CannotPerformOperationException;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper service for batch creation of users.
 *
 */
@Service
public class BatchCreationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchCreationService.class);

  private final UserService userService;


  @Inject
  public BatchCreationService(final UserService userService) {
    this.userService = userService;
  }

  /**
   * Creates and persists a set of users.
   *
   * @param batch the batch request
   * @return as list of all users created
   * @throws UserCrudException if the batch creation was unsuccessful. If this exception is thrown,
   *         no user is persisted.
   */
  public List<User> create(final UserBatchRequest batch) throws UserCrudException {

    if (batch.getPasswords().size() != batch.getCount()) {
      throw new UserCrudException(
          "Amount of passwords does not match the amount of users to create.");
    }

    final List<User> created = new ArrayList<>();
    for (int i = 0; i < batch.getCount(); i++) {

      User newUser = null;
      try {
        newUser = this.newUser(batch.getPrefix(), i, batch.getPasswords().get(i), batch.getRoles());
      } catch (final CannotPerformOperationException e1) {
        if (LOGGER.isErrorEnabled()) {
          LOGGER.error("Batch request failed, rolling back.");
        }
        this.rollbackBatch(created);
        throw new UserCrudException("Could not hash password");
      }


      User currentCreated = null;
      try {
        currentCreated = this.userService.saveNewEntity(newUser);
      } catch (final UserCrudException e) {
        if (LOGGER.isWarnEnabled()) {
          LOGGER.warn("Batch request failed, rolling back.");
        }
        this.rollbackBatch(created);
        throw e;
      }

      created.add(currentCreated);

    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Created a batch of %d users", created.size()));
    }
    return created;
  }


  private void rollbackBatch(final List<User> created) {
    for (final User user : created) {
      try {
        this.userService.deleteEntityById(user.getId());
      } catch (final UserCrudException e) {
        // This should never happen
        if (LOGGER.isErrorEnabled()) {
          LOGGER.error(String.format("Rollback failed for user with id", user.getId()), e);
        }
      }
    }
  }

  private User newUser(final String pref, final int num, final String password,
      final List<Role> roles) throws CannotPerformOperationException {
    final StringBuilder sb = new StringBuilder();
    final String name = sb.append(pref).append('-').append(num).toString();

    // hash password
    final String hashed = PasswordStorage.createHash(password);


    return new User(null, name, hashed, roles);
  }

}
