package net.explorviz.security.services;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import net.explorviz.security.model.UserCredentials;
import net.explorviz.security.util.PasswordStorage;
import net.explorviz.security.util.PasswordStorage.CannotPerformOperationException;
import net.explorviz.security.util.PasswordStorage.InvalidHashException;
import net.explorviz.shared.security.User;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Injectable service that contains utility methods for {@link UserCredentials} validation.
 */
@Service
public class UserValidationService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(UserValidationService.class.getSimpleName());


  @Inject
  private UserCrudService userCrudService;

  /**
   * This method validates the passed {@link UserCredentials}, therefore enables overall
   * authentication for this web service and the token generation.
   *
   * @param userCredentials -
   * @return
   * @throws InvalidHashException
   * @throws CannotPerformOperationException
   */
  public User validateUserCredentials(final UserCredentials userCredentials)
      throws CannotPerformOperationException, InvalidHashException {

    if (!this.checkIfDataIsNotNull(userCredentials)) {
      throw new ForbiddenException("Enter username and password");
    }

    final User user = this.userCrudService.findUserByName(userCredentials.getUsername())
        .orElseThrow(() -> new ForbiddenException("Wrong username or password"));



    if (!PasswordStorage.verifyPassword(userCredentials.getPassword(), user.getPassword())) {
      throw new ForbiddenException("Wrong username or password");
    }


    return user;


  }

  private boolean checkIfDataIsNotNull(final UserCredentials credentials) {
    return credentials != null && credentials.getUsername() != null
        && credentials.getPassword() != null;
  }



}
