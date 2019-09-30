package net.explorviz.settings.server.resources.test.helper;

import com.github.jasminb.jsonapi.exceptions.ResourceParseException;
import io.restassured.mapper.ObjectMapperType;
import java.util.Optional;
import net.explorviz.shared.security.model.User;

import static io.restassured.RestAssured.given;

public class AuthorizationHelper {

  private static final String AUTH_URL = "http://localhost:8082/v1/tokens/";
  private static final String ADMIN_NAME = "admin";
  private static final String NORMIE_NAME = "normie";
  private static final String ADMIN_PW = "password";
  private static final String NORMIE_PW = "password";


  private static String normieToken = null;
  private static String adminToken = null;

  private static User admin = null;
  private static User normie = null;

  public static Optional<User> login(String name, String password) {

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
    return getNormie().getToken();
  }

  public static String getAdminToken() {
    return getAdmin().getToken();
  }

  public static User getNormie() {
    Optional<User> normie = login(NORMIE_NAME, NORMIE_PW);
    if(AuthorizationHelper.normie == null) {
      if (normie.isPresent()) {
        AuthorizationHelper.normie = normie.get();
      } else {
        // Not existent, create and try again
        // Will fail if normie user exists with another password
        Optional<User> created_normie =
            UsersHelper.getInstance().createUser(NORMIE_NAME, NORMIE_PW, null);
        if (created_normie.isPresent()) {
          return getNormie();
        } else {
          throw new IllegalStateException("Can no login as normie, does no exist");
        }
      }
    }

    return AuthorizationHelper.normie;
  }

  public static User getAdmin() {
    if (AuthorizationHelper.admin == null) {
      Optional<User> admin = login(ADMIN_NAME, ADMIN_PW);
      if (admin.isPresent()) {
        AuthorizationHelper.admin = admin.get();
      } else {
        throw new IllegalStateException("No default admin in database, aborting.");
      }
    }
    return AuthorizationHelper.admin;
  }


  static class UserCredentials {
    public String username;
    public String password;

    public UserCredentials(String username, String password) {
      this.username = username;
      this.password = password;
    }
  }


}

