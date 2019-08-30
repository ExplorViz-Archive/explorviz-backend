package net.explorviz.security.server;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import net.explorviz.security.model.UserCredentials;
import net.explorviz.security.server.helper.JsonAPIMapper;
import net.explorviz.security.server.helper.MongoHelper;
import net.explorviz.security.server.helper.UserSerializationHelper;
import net.explorviz.shared.security.model.User;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import net.explorviz.shared.security.model.roles.Role;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class UserCreation {

  private final static String AUTH_ROUTE = "http://localhost:8082/v1/tokens";
  private final static String ADMIN_NAME = "admin";
  private final static String ADMIN_PW = "password";

  private final static String BASE_URI = "http://localhost:8082/v1/";

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private static String adminToken;
  private static String normieToken;

  private Header authHeaderAdmin;
  private Header authHeaderNormie;

  private MongoHelper mongoHelper;


  /**
   * Retrieves token for both an admin and an unprivileged user ("normie").
   * The default admin is used for the former, a normie is created.
   *
   * @throws IOException if serialization fails
   */
  @BeforeAll static void setUpAll() throws IOException {
    // Use default admin
    User admin = given().
        body(new UserCredentials(ADMIN_NAME, ADMIN_PW)).
        contentType(ContentType.JSON).
    when().
        post(AUTH_ROUTE).
        as(User.class, new JsonAPIMapper<>(User.class));
    adminToken = admin.getToken();

    // Create normie user
    User normie = new User(null, "normie", "password", null);
    given().
        body(UserSerializationHelper.serialize(normie)).
        header(new Header("authorization", "Bearer " + adminToken)).
        contentType(MEDIA_TYPE).
      when().
        post(BASE_URI + "users/");

    normie = given().
        body(new UserCredentials("normie", "password")).
        contentType(ContentType.JSON).
      when().
        post(AUTH_ROUTE).
        as(User.class, new JsonAPIMapper<>(User.class));

    normieToken = normie.getToken();
  }

  @BeforeEach void setUp() {
    this.authHeaderAdmin = new Header("authorization", "Bearer " + adminToken);
    this.authHeaderNormie = new Header("authorization", "Bearer " + normieToken);
    this.mongoHelper = new MongoHelper(MongoHelper.USER_MONGO_HOST);
  }

  @AfterEach void tearDown() {
    mongoHelper.emptyCollection(MongoHelper.UserCollections.USERS.name());
    mongoHelper.dropDB();
    mongoHelper.close();
  }

  @Test @DisplayName("Create a valid user without roles") public void createValidWithoutRoles()
      throws IOException {
    final String name = "testuser";
    final String password = "password";
    User u = new User(null, name, password, null);

    given().
        body(UserSerializationHelper.serialize(u)).
        contentType(MEDIA_TYPE).
        header(authHeaderAdmin).
    when().
        post(BASE_URI + "users/").
    then().
        statusCode(200).
        body("$", hasKey("data")).
        body("data.attributes.username", is(name)).
        body("data", not(hasKey("relationship")));
  }

  @Test @DisplayName("Create a new admin") public void createValidAdmin() throws IOException {
    final String name = "testadmin";
    final String password = "password";
    User u = new User(null, name, password, new ArrayList<Role>(Arrays.asList(new Role("admin"))));

    given().
        body(UserSerializationHelper.serialize(u)).
        contentType(MEDIA_TYPE).
        header(authHeaderAdmin).
    when().
        post(BASE_URI + "users/").
    then().
        statusCode(200).
        body("$", hasKey("data")).
        body("data.attributes.username", is(name)).
        body("data", hasKey("relationships")).
        body("data.relationships", hasKey("roles")).
        body("data.relationships.roles.data.size()", is(1)).
        body("data.relationships.roles.data[0]", hasEntry("id", "admin"));
  }

  @Test @DisplayName("Login with created user") public void loginWithCreatedUser()
      throws IOException {

    final String name = "testuser";
    final String password = "password";

    User u = new User(null, name, password, null);

    // Create user
    given().body(UserSerializationHelper.serialize(u)).header(authHeaderAdmin).contentType(MEDIA_TYPE)
        .when().post(BASE_URI + "users/");

    given().body(new UserCredentials(name, password)).contentType(ContentType.JSON).when()
        .post(AUTH_ROUTE).then().statusCode(200).body("$", hasKey("data"))
        .body("data.attributes", hasKey("token"));

  }


  @Test @DisplayName("Create user without password") void createUserWithNoPassword() {
    User u =
        new User(null, "name", null, null);

    given().
        body(u, new JsonAPIMapper<>(User.class)).
        contentType(MEDIA_TYPE).
        header(authHeaderAdmin).
    when().
        post(BASE_URI + "users/").
    then().
        statusCode(400);
  }


  @Test @DisplayName("Create user unauthenticated") void createUserUnauthenticated() {
    User u =
        new User(null, "name", null, null);

    given().
        body(u, new JsonAPIMapper<>(User.class)).
        contentType(MEDIA_TYPE).
    when().
        post(BASE_URI + "users/").
    then().
        statusCode(403);
  }

  @Test @DisplayName("Create user unauthenticated") void createUserAsNormie() {
    User u =
        new User(null, "name", null, null);

    given().
        body(u, new JsonAPIMapper<>(User.class)).
        contentType(MEDIA_TYPE).
        header(authHeaderNormie).
    when().
        post(BASE_URI + "users/").
    then().
        statusCode(403);
  }
}
