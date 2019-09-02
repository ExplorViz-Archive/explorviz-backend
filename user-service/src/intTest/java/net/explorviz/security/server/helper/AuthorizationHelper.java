package net.explorviz.security.server.helper;

import net.explorviz.security.server.main.DependencyInjectionBinder;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserService;
import net.explorviz.security.services.exceptions.UserCrudException;
import net.explorviz.security.util.PasswordStorage;
import net.explorviz.shared.security.model.User;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import javax.inject.Inject;

public class AuthorizationHelper {


  private static AuthorizationHelper instance = null;

  public static AuthorizationHelper getInstance(){
    if (instance == null) {
      DependencyInjectionBinder dib = new DependencyInjectionBinder();
      ServiceLocator locator = ServiceLocatorUtilities.bind(dib);
      instance = locator.createAndInitialize(AuthorizationHelper.class);
    }
    return instance;
  }

  private final String normieToken;
  private final String adminToken;

  @Inject
  private AuthorizationHelper(UserService us, TokenService ts)
      throws PasswordStorage.CannotPerformOperationException, UserCrudException {
    if (!us.findEntityByFieldValue("username", "normie").isPresent()) {
      // Create normie user
      User normie = new User(null, "normie",
          PasswordStorage.createHash("password"), null);

      us.saveNewEntity(normie);
    }

    // get tokens
    User normie = us.findEntityByFieldValue("username", "normie").get();
    User admin = us.findEntityByFieldValue("username", "admin").get();
    normieToken = ts.issueNewToken(normie);
    adminToken = ts.issueNewToken(admin);
  }

  public String getNormieToken() {
    return normieToken;
  }

  public String getAdminToken() {
    return adminToken;
  }
}
