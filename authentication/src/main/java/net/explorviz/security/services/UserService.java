package net.explorviz.security.services;

import javax.ws.rs.ForbiddenException;
import net.explorviz.security.model.UserCredentials;
import net.explorviz.shared.security.User;
import org.jvnet.hk2.annotations.Service;

@Service
public class UserService {

  /**
   * This method validates the passed {@link UserCredentials}, therefore enables overall
   * authentication for this web service and the token generation.
   *
   * @param userCredentials -
   * @return
   */
  public User validateUserCredentials(final UserCredentials userCredentials) {

    if (!checkIfDataIsNotNull(userCredentials)) {
      throw new ForbiddenException("Enter username and password");
    }

    // TODO valid DB query
    if (userCredentials.getUsername().equals("admin") // NOCS
        && userCredentials.getPassword().equals("password")) {

      final User user = new User("admin"); // NOCS

      // For Testing,
      user.getRoles().add("admin"); // NOCS

      return user;
    } else {
      throw new ForbiddenException("Wrong username or password");
    }
  }

  private boolean checkIfDataIsNotNull(final UserCredentials credentials) {
    if (credentials != null && credentials.getUsername() != null
        && credentials.getPassword() != null) {
      return true;
    }

    return false;
  }

}
