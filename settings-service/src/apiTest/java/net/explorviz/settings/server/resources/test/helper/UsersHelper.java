package net.explorviz.settings.server.resources.test.helper;

import static io.restassured.RestAssured.given;
import com.github.jasminb.jsonapi.exceptions.ResourceParseException;
import io.restassured.http.Header;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import net.explorviz.security.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for manipulating users through HTTP. Represents a minimal client to the user API.
 * All requests are performed as the default admin.
 */
public class UsersHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(UsersHelper.class);

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private final static String USERS_URI = "http://localhost:8090/v1/users/";

  public static UsersHelper getInstance() {
    if (instance == null) {
      instance = new UsersHelper();
    }
    return instance;
  }

  private static UsersHelper instance = null;

  private final Header auth;

  private UsersHelper() {
    final String tok = AuthorizationHelper.getAdminToken();
    this.auth = new Header("authorization", "Bearer " + tok);
  }


  /**
   * Creates a new user by issuing a post request.
   *
   * @param name name of the user
   * @param password password of the user
   * @param roles roles of the user
   * @return An optional containing the created user as returned by the API or null if an error
   *         occured.
   */
  public Optional<User> createUser(final String name, final String password,
      final List<String> roles) {
    final User toCreate = new User(null, name, password, roles);

    try {
      final User u = given().contentType(MEDIA_TYPE)
          .body(UserSerializationHelper.serialize(toCreate))
          .header(this.auth)
          .when()
          .post(USERS_URI)
          .as(User.class, new JsonAPIMapper<>(User.class));
      return Optional.of(u);
    } catch (final IOException e) {
      return Optional.empty();
    } catch (final ResourceParseException e) {
      LOGGER.error("User not created", e);
      return Optional.empty();
    }
  }

  /**
   * Delete a user by id
   *
   * @param id if of the user to delete
   */
  public void deleteUserById(final String id) {
    given().contentType(MEDIA_TYPE).header(this.auth).when().delete(USERS_URI + id);
  }


  public List<User> getAll() {
    return given().contentType(MEDIA_TYPE)
        .header(this.auth)
        .when()
        .get(USERS_URI)
        .as(List.class, new JsonAPIListMapper<>(User.class));
  }

  public int count() {
    return this.getAll().size();
  }

}
