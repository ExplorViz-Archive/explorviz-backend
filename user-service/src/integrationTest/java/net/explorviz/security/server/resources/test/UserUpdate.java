package net.explorviz.security.server.resources.test;

import io.restassured.http.Header;
import io.restassured.mapper.ObjectMapperType;
import net.explorviz.security.model.UserCredentials;
import net.explorviz.security.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.security.server.resources.test.helper.JsonAPIMapper;
import net.explorviz.security.server.resources.test.helper.UserSerializationHelper;
import net.explorviz.security.server.resources.test.helper.UsersHelper;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;

public class UserUpdate {


  private final static String BASE_URI = "http://localhost:8082/v1/";

  private final String USER_PW = "pass";
  private final String USER_NAME = "user";


  private static String adminToken;
  private static String normieToken;

  private Header authHeaderAdmin;
  private Header authHeaderNormie;

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private User theUser;
  private String userUri;

  @BeforeAll static void setUpAll() throws IOException {
    adminToken = AuthorizationHelper.getAdminToken();
    normieToken = AuthorizationHelper.getNormieToken();
  }

  @BeforeEach void setUp() {
    this.authHeaderAdmin = new Header("authorization", "Bearer " + adminToken);
    this.authHeaderNormie = new Header("authorization", "Bearer " + normieToken);

    Optional<User> opt = UsersHelper.getInstance()
        .createUser(USER_NAME, USER_PW, null);

    if (opt.isPresent()) {
      theUser = opt.get();
      userUri = BASE_URI+"users/" + theUser.getId();
    } else {
      Assertions.fail();
    }

  }

  @AfterEach void tearDown() {
    UsersHelper.getInstance().deleteUserById(theUser.getId());
  }

  @Test
  void changePassword() throws IOException {

    final String newpw = "newpw";
    User changeTo = new User(null, USER_NAME, newpw, null);

    // Perform patch
    given()
        .header(authHeaderAdmin)
        .contentType(MEDIA_TYPE)
        .body(UserSerializationHelper.serialize(changeTo))
        .when()
        .patch(userUri)
        .then()
        .statusCode(200);

    // Try to login with new pw

    given()
        .body(new UserCredentials(USER_NAME, newpw), ObjectMapperType.JACKSON_2)
        .contentType("application/json")
        .when()
        .post(BASE_URI+"tokens")
        .then()
        .statusCode(200)
        .body("data.attributes", hasKey("token"));

  }


  @Test
  void changeName() throws IOException {

    final String newname = "newname";
    User changeTo = new User(null, newname, USER_PW, null);

    // Perform patch
    given()
        .header(authHeaderAdmin)
        .contentType(MEDIA_TYPE)
        .body(UserSerializationHelper.serialize(changeTo))
        .when()
        .patch(userUri)
        .then()
        .statusCode(200);

    // Try to login with new name
    given()
        .body(new UserCredentials(newname, USER_PW), ObjectMapperType.JACKSON_2)
        .contentType("application/json")
        .when()
        .post(BASE_URI+"tokens")
        .then()
        .statusCode(200)
        .body("data.attributes", hasKey("token"));

  }

  @Test
  void updateAsNormie() throws IOException {
    final String newname = "newname";
    User changeTo = new User(null, newname, USER_PW, null);

    // Perform patch
    given()
        .header(authHeaderNormie)
        .contentType(MEDIA_TYPE)
        .body(UserSerializationHelper.serialize(changeTo))
        .when()
        .patch(userUri)
        .then()
        .statusCode(403);

  }

  @Test
  void addRole() throws IOException {
    final List<Role> adminRole = new ArrayList<Role>(Arrays.asList(new Role("admin")));
    User changeTo = new User(null, USER_NAME, USER_PW, adminRole);

    // Perform patch
    User updated = given()
        .header(authHeaderAdmin)
        .contentType(MEDIA_TYPE)
        .body(UserSerializationHelper.serialize(changeTo))
        .when()
        .patch(userUri)
        .then()
        .statusCode(200)
        .extract().body().as(User.class, new JsonAPIMapper<User>(User.class));

    assertTrue(updated.getRoles().stream()
        .map(r -> r.getDescriptor()).anyMatch(d -> d.contentEquals("admin")));
  }



  @Test
  void updateId() throws IOException {
    User changeTo = new User("someid", USER_NAME, USER_PW, null);

    // Perform patch
    given()
        .header(authHeaderAdmin)
        .contentType(MEDIA_TYPE)
        .body(UserSerializationHelper.serialize(changeTo))
        .when()
        .patch(userUri)
        .then()
        .statusCode(400);
  }

}
