package net.explorviz.security.server.helper;

import com.github.jasminb.jsonapi.exceptions.ResourceParseException;
import io.restassured.http.Header;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;

/**
 * Helper class for manipulating users through HTTP.
 * Represents a minimal client to the user API.
 */
public class UsersHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(UsersHelper.class);

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private final static String USERS_URI = "http://localhost:8082/v1/users";

  public static UsersHelper getInstance() {
    if (instance == null) {
      instance = new UsersHelper();
    }
    return instance;
  }

  private static UsersHelper instance = null;

  private Header auth;

  private  UsersHelper() {
    String tok = AuthorizationHelper.getAdminToken();
    auth = new Header("authorization", "Bearer "+tok);
  }


  /**
   * Creates a new user by issuing a post request.
   *
   * @param name name of the user
   * @param password password of the user
   * @param roles roles of the user
   * @return An optional containing the created user as returned by the API or null if an error
   * occured.
   */
  public Optional<User> createUser(String name, String password, List<Role> roles) {
    User toCreate = new User(null, name, password, roles);

    try {
      User u = given()
          .contentType(MEDIA_TYPE)
          .body(UserSerializationHelper.serialize(toCreate))
          .header(auth)
          .when()
          .post(USERS_URI)
          .as(User.class, new JsonAPIMapper<User>(User.class));
      return Optional.of(u);
    } catch (IOException e) {
      return Optional.empty();
    } catch (ResourceParseException e) {
      LOGGER.error("User not created", e);
      return Optional.empty();
    }
  }

}
