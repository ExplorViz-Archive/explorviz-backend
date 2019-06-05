package net.explorviz.security.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import net.explorviz.security.model.UserBatchRequest;
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

  private final UserMongoCrudService userService;


  @Inject
  public BatchCreationService(final UserMongoCrudService userService) {
    this.userService = userService;
  }

  /**
   * Creates and persists a set of users.
   *
   * @param batch the batch request
   * @return as list of all users created
   */
  public List<User> create(final UserBatchRequest batch) {
    final List<User> created = new ArrayList<>();
    for (int i = 0; i < batch.getCount(); i++) {
      final User newUser =
          this.newUser(batch.getPrefix(), i, batch.getPassword(), batch.getRoles());

      final Optional<User> currentCreated = this.userService.saveNewEntity(newUser);
      if (currentCreated.isPresent()) {
        created.add(currentCreated.get());
      }
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Created a batch of %d users", created.size()));
    }
    return created;
  }


  private User newUser(final String pref, final int num, final String password,
      final List<Role> roles) {
    final StringBuilder sb = new StringBuilder();
    final String name = sb.append(pref).append('-').append(num).toString();
    return new User(null, name, password, roles);
  }

}
