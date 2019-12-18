package net.explorviz.settings.server.resources.test.helper;

import static io.restassured.RestAssured.given;
import com.github.jasminb.jsonapi.exceptions.ResourceParseException;
import io.restassured.mapper.ObjectMapperType;
import java.util.Optional;
import net.explorviz.security.user.User;

public class AuthorizationHelper {

  private static final String AUTH_URL = "http://localhost:8090/v1/tokens/";
  private static final String ADMIN_NAME = "admin";
  private static final String NORMIE_NAME = "normie";
  private static final String ADMIN_PW = "password";
  private static final String NORMIE_PW = "password";


  private static String normieToken = null;
  private static String adminToken = null;

  private static User admin = null;
  private static User normie = null;

  public static Optional<User> login(final String name, final String password) {

    try {
      final User u = given().contentType("application/json")
          .body(new UserCredentials(name, password), ObjectMapperType.JACKSON_2)
          .when()
          .post(AUTH_URL)
          .as(User.class, new JsonAPIMapper<>(User.class));
      return Optional.of(u);
    } catch (final ResourceParseException ex) {
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
    final Optional<User> normie = login(NORMIE_NAME, NORMIE_PW);
    if (AuthorizationHelper.normie == null) {
      if (normie.isPresent()) {
        AuthorizationHelper.normie = normie.get();
      } else {
        // Not existent, create and try again
        // Will fail if normie user exists with another password
        final Optional<User> created_normie =
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
      final Optional<User> admin = login(ADMIN_NAME, ADMIN_PW);
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

    public UserCredentials(final String username, final String password) {
      this.username = username;
      this.password = password;
    }
  }


}

