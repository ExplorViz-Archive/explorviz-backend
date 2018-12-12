package net.explorviz.security.services;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import net.explorviz.security.model.UserCredentials;
import net.explorviz.security.util.PasswordStorage;
import net.explorviz.security.util.PasswordStorage.CannotPerformOperationException;
import net.explorviz.security.util.PasswordStorage.InvalidHashException;
import net.explorviz.shared.security.model.User;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Injectable service that contains utility methods for {@link UserCredentials} validation.
 */
@Service
public class UserValidationService {

  private static final String MSG_WRONGCRED = "Wrong username or password";

  private static final Logger LOGGER = // NOPMD
      LoggerFactory.getLogger(UserValidationService.class.getSimpleName());


  @Inject
  private UserMongoCrudService userCrudService;

  /**
   * This method validates the passed {@link UserCredentials}, therefore enables overall
   * authentication for this web service and the token generation.
   *
   * @param userCredentials username and password
   * @return the user, if the token credentials were valid
   */
  public User validateUserCredentials(final UserCredentials userCredentials) {

    if (!this.checkIfDataIsNotNull(userCredentials)) {
      throw new ForbiddenException("Enter username and password");
    }

    final User user =
        this.userCrudService.findEntityByFieldValue("username", userCredentials.getUsername())
            .orElseThrow(() -> new ForbiddenException(MSG_WRONGCRED));

    try {
      if (!PasswordStorage.verifyPassword(userCredentials.getPassword(), user.getPassword())) {
        throw new ForbiddenException(MSG_WRONGCRED);
      }
    } catch (CannotPerformOperationException | InvalidHashException e) {
      throw new ForbiddenException(MSG_WRONGCRED, e);
    }


    return user;


  }

  private boolean checkIfDataIsNotNull(final UserCredentials credentials) {
    return credentials != null && credentials.getUsername() != null
        && credentials.getPassword() != null;
  }



}
