package net.explorviz.security.services;

import java.util.List;
import net.explorviz.shared.security.User;
import org.jvnet.hk2.annotations.Service;

/**
 * Injectable service that manages create, read, update and delete operations for {@link User}
 * objects.
 *
 * @author lotzk
 *
 */
@Service
public class UserCrudService {

  /**
   * Persists a new user object.
   *
   * @param user the user to persist
   */
  public User saveNewUser(final User user) {
    // Todo: Auto-Generate id

    return null;
  }


  /**
   * Updates values of an existing user.
   *
   * @param user the user to update
   */
  public void updateUser(final User user) {

    return;
  }

  /**
   * Retrieves a user by its id.
   *
   * @param id the id of the user to find
   * @return as {@link User} object of the user with the given id or {@code null}, if no such user
   *         exists
   */
  public User getUserById(final Long id) {

    return null;
  }

  /**
   * Retrieves a list of all users, that have a specific role assigned.
   *
   * @param role role to search for
   * @return a list of all users, that have the given role assigned
   */
  public List<User> getUsersByRole(final String role) {

    return null;
  }


  /**
   * Deletes a user.
   *
   * @param id the id of the user to delete
   */
  public void deleteUserById(final Long id) {

  }



}
