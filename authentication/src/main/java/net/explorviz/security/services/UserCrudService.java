package net.explorviz.security.services;

import java.util.List;
import net.explorviz.shared.security.User;
import org.jvnet.hk2.annotations.Service;

/**
 * Injectable service that manages create, read, update and delete operations for {@link User}
 * objects.
 *
 */
@Service
public interface UserCrudService {

  /**
   * Persists a new user object.
   *
   * @param user the user to persist
   */
  User saveNewUser(final User user);


  /**
   * Updates values of an existing user.
   *
   * @param user the user to update
   */
  void updateUser(final User user);

  /**
   * Retrieves a user by its id.
   *
   * @param id the id of the user to find
   * @return as {@link User} object of the user with the given id or {@code null}, if no such user
   *         exists
   */
  User getUserById(final Long id);


  /**
   * Retrieves all users.
   *
   * @return a list of all known users
   */
  List<User> getAll();

  /**
   * Retrieves a list of all users, that have a specific role assigned.
   *
   * @param role role to search for
   * @return a list of all users, that have the given role assigned
   */
  List<User> getUsersByRole(final String role);


  /**
   * Deletes a user.
   *
   * @param id the id of the user to delete
   */
  void deleteUserById(final Long id);


}
