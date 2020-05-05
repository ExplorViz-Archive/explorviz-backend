package net.explorviz.security.server.resources.test.helper;

import static io.restassured.RestAssured.given;

import com.github.jasminb.jsonapi.exceptions.ResourceParseException;
import io.restassured.mapper.ObjectMapperType;
import java.util.Optional;
import net.explorviz.security.model.UserCredentials;
import net.explorviz.security.user.User;

/**
 * Utility class to perform the login process in other test cases.
 */
public final class AuthorizationHelper {



  private static final String AUTH_URL = "http://localhost:8090/v1/tokens/";
  private static final String ADMIN_NAME = "admin";
  private static final String NORMIE_NAME = "normie";
  private static final String ADMIN_PW = "password";
  private static final String NORMIE_PW = ADMIN_PW;


  private static User admin;
  private static User normie;

  private AuthorizationHelper(){/* Utility */}

  private static Optional<User> login(final String name, final String password) {

    try {
      final User u = given().contentType("application/json")
          .body(new UserCredentials(name, password), ObjectMapperType.JACKSON_2)
          .when()
          .post(AUTH_URL)
          .as(User.class, new JsonApiMapper<>(User.class));
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

  /**
   * Return a {@link User} without no roles, i.e., without any elevated permissions.
   */
  public static User getNormie() {
    final Optional<User> normie = login(NORMIE_NAME, NORMIE_PW);
    if (AuthorizationHelper.normie == null) {
      if (normie.isPresent()) {
        AuthorizationHelper.normie = normie.get();
      } else {
        // Not existent, create and try again
        // Will fail if normie user exists with another password
        final Optional<User> createdNormie =
            UsersHelper.getInstance().createUser(NORMIE_NAME, NORMIE_PW, null);
        if (createdNormie.isPresent()) {
          return getNormie();
        } else {
          throw new IllegalStateException("Can no login as normie, does no exist");
        }
      }
    }

    return AuthorizationHelper.normie;
  }

  /**
   * Returns a {@link User} with the admin role assigned.
   */
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


}

