package net.explorviz.security.server.helper;

import com.github.jasminb.jsonapi.exceptions.ResourceParseException;
import io.restassured.mapper.ObjectMapperType;
import net.explorviz.security.model.UserCredentials;
import net.explorviz.security.server.main.DependencyInjectionBinder;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserService;
import net.explorviz.security.services.exceptions.UserCrudException;
import net.explorviz.security.util.PasswordStorage;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import javax.inject.Inject;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.options;

public class AuthorizationHelper {

  private static final String AUTH_URL = "http://localhost:8082/v1/tokens/";
  private static final String ADMIN_NAME = "admin";
  private static final String NORMIE_NAME = "normie";
  private static final String ADMIN_PW = "password";
  private static final String NORMIE_PW = "password";


  private static String normieToken = null;
  private static String adminToken = null;

    private static Optional<User> login(String name, String password) {

      try {
        User u = given().contentType("application/json")
            .body(new UserCredentials(name, password), ObjectMapperType.JACKSON_2)
            .when()
            .post(AUTH_URL).as(User.class, new JsonAPIMapper<User>(User.class));
        return Optional.of(u);
      } catch(ResourceParseException ex) {
        return Optional.empty();
      }
  }



  public static String getNormieToken() {
    Optional<User> normie = login(NORMIE_NAME, NORMIE_PW);
    if(normieToken == null) {
      if (normie.isPresent()) {
        normieToken = normie.get().getToken();
      } else {
        // Not existent, create and try again
        // Will fail if normie user exists with another password
        Optional<User> created_normie =
            UsersHelper.getInstance().createUser(NORMIE_NAME, NORMIE_PW, null);
        if (created_normie.isPresent()) {
          return getNormieToken();
        } else {
          throw new IllegalStateException("Can no login as normie, does no exist");
        }
      }
    }

    return normieToken;
  }

  public static String getAdminToken() {
    if (adminToken == null) {
      Optional<User> admin = login(ADMIN_NAME, ADMIN_PW);
      if (admin.isPresent()) {
        adminToken = admin.get().getToken();
      } else {
        throw new IllegalStateException("No default admin in database, aborting.");
      }
    }
    return adminToken;
  }


}

