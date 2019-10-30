package net.explorviz.security.server.resources.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.explorviz.security.model.UserCredentials;
import net.explorviz.security.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.security.server.resources.test.helper.JsonAPIMapper;
import net.explorviz.security.server.resources.test.helper.UserSerializationHelper;
import net.explorviz.security.user.User;
import net.explorviz.security.user.Role;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserCreation {

  private final static String AUTH_ROUTE = "http://localhost:8090/v1/tokens";
  private final static String BASE_URI = "http://localhost:8090/v1/";

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private static String adminToken;
  private static String normieToken;

  private Header authHeaderAdmin;
  private Header authHeaderNormie;



  /**
   * Retrieves token for both an admin and an unprivileged user ("normie"). The default admin is
   * used for the former, a normie is created.
   *
   * @throws IOException if serialization fails
   */
  @BeforeAll
  static void setUpAll() throws IOException {
    adminToken = AuthorizationHelper.getAdminToken();
    normieToken = AuthorizationHelper.getNormieToken();
  }

  @BeforeEach
  void setUp() {
    this.authHeaderAdmin = new Header("authorization", "Bearer " + adminToken);
    this.authHeaderNormie = new Header("authorization", "Bearer " + normieToken);
  }


  @Test
  @DisplayName("Create a valid user without roles")
  public void createValidWithoutRoles() throws IOException {
    final String name = "testuser2";
    final String password = "password";
    final User u = new User(null, name, password, null);

    given().body(UserSerializationHelper.serialize(u))
        .contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .when()
        .post(BASE_URI + "users/")
        .then()
        .statusCode(200)
        .body("$", hasKey("data"))
        .body("data.attributes.username", is(name))
        .body("data", not(hasKey("relationship")));
  }

  @Test
  @DisplayName("Create a new admin")
  public void createValidAdmin() throws IOException {
    final String name = "testadmin";
    final String password = "password";
    final List<String> roles = Arrays.asList(Role.ADMIN_NAME);

    final User u = new User(null, name, password, roles);

    given().body(UserSerializationHelper.serialize(u))
        .contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .when()
        .post(BASE_URI + "users/")
        .then()
        .statusCode(200)
        .body("$", hasKey("data"))
        .body("data.attributes.username", is(name))
        .body("data.attributes.roles", is(roles));
  }

  @Test
  @DisplayName("Login with created user")
  public void loginWithCreatedUser() throws IOException {

    final String name = "testuser";
    final String password = "password";

    final User u = new User(null, name, password, null);

    // Create user
    given().body(UserSerializationHelper.serialize(u))
        .header(this.authHeaderAdmin)
        .contentType(MEDIA_TYPE)
        .when()
        .post(BASE_URI + "users/");

    given().body(new UserCredentials(name, password))
        .contentType(ContentType.JSON)
        .when()
        .post(AUTH_ROUTE)
        .then()
        .statusCode(200)
        .body("$", hasKey("data"))
        .body("data.attributes", hasKey("token"));

  }


  @Test
  @DisplayName("Create user without password")
  void createUserWithNoPassword() {
    final User u = new User(null, "name", null, null);

    given().body(u, new JsonAPIMapper<>(User.class))
        .contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .when()
        .post(BASE_URI + "users/")
        .then()
        .statusCode(400);
  }


  @Test
  @DisplayName("Create user without token.")
  void createUserUnauthenticated() {
    final User u = new User(null, "name", null, null);

    given().body(u, new JsonAPIMapper<>(User.class))
        .contentType(MEDIA_TYPE)
        .when()
        .post(BASE_URI + "users/")
        .then()
        .statusCode(401);
  }

  @Test
  @DisplayName("Create user unauthenticated.")
  void createUserAsNormie() {
    final User u = new User(null, "name", null, null);

    given().body(u, new JsonAPIMapper<>(User.class))
        .contentType(MEDIA_TYPE)
        .header(this.authHeaderNormie)
        .when()
        .post(BASE_URI + "users/")
        .then()
        .statusCode(401);
  }
}
