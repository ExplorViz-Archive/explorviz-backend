package net.explorviz.security.services;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import net.explorviz.security.util.CountingIdGenerator;
import net.explorviz.security.util.IdGenerator;
import net.explorviz.shared.security.User;
import org.jvnet.hk2.annotations.Service;

/**
 * Helper class, that stores user object within a map.
 */
@Service
public class InMemoryUserCrudService implements UserCrudService {

  private final Map<Long, User> userDb;
  private final IdGenerator<Long> idGen;

  /**
   * Creates new {@link InMemoryUserCrudService}.
   */
  public InMemoryUserCrudService() {
    // Use tree map to have sorted keys, use reverseOrder to get largest key in O(1)
    this.userDb = new TreeMap<>(Collections.reverseOrder());
    this.idGen = new CountingIdGenerator();
  }

  @Override
  public User saveNewUser(final User user) {
    final User persistedUser =
        new User(this.idGen.next(), user.getUsername(), user.getPassword(), user.getRoles());
    this.userDb.put(persistedUser.getId(), persistedUser);
    return persistedUser;
  }

  @Override
  public void updateUser(final User user) {
    if (user.getId() != null) {
      this.userDb.put(user.getId(), user);
    }
  }

  @Override
  public User getUserById(final Long id) {
    return this.userDb.get(id);
  }

  @Override
  public List<User> getUsersByRole(final String role) {
    return this.userDb.values().stream().filter(u -> u.getRoles() != null)
        .filter(u -> u.getRoles().contains(role)).collect(Collectors.toList());
  }

  @Override
  public void deleteUserById(final Long id) {
    if (id != null) {
      this.userDb.remove(id);
    }
  }

}
