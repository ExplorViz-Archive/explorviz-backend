package net.explorviz.security.server.resources.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;
import io.restassured.http.Header;
import io.restassured.mapper.ObjectMapperType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.explorviz.security.model.UserCredentials;
import net.explorviz.security.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.security.server.resources.test.helper.JsonAPIMapper;
import net.explorviz.security.server.resources.test.helper.UserSerializationHelper;
import net.explorviz.security.server.resources.test.helper.UsersHelper;
import net.explorviz.security.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserUpdate {


  private final static String BASE_URI = "http://localhost:8090/v1/";

  private final String USER_PW = "pass";
  private final String USER_NAME = "user";


  private static String adminToken;
  private static String normieToken;

  private Header authHeaderAdmin;
  private Header authHeaderNormie;

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private User theUser;
  private String userUri;

  @BeforeAll
  static void setUpAll() throws IOException {
    adminToken = AuthorizationHelper.getAdminToken();
    normieToken = AuthorizationHelper.getNormieToken();
  }

  @BeforeEach
  void setUp() {
    this.authHeaderAdmin = new Header("authorization", "Bearer " + adminToken);
    this.authHeaderNormie = new Header("authorization", "Bearer " + normieToken);

    final Optional<User> opt =
        UsersHelper.getInstance().createUser(this.USER_NAME, this.USER_PW, null);

    if (opt.isPresent()) {
      this.theUser = opt.get();
      this.userUri = BASE_URI + "users/" + this.theUser.getId();
    } else {
      Assertions.fail();
    }

  }

  @AfterEach
  void tearDown() {
    UsersHelper.getInstance().deleteUserById(this.theUser.getId());
  }

  @Test
  void changePassword() throws IOException {

    final String newpw = "newpw";
    final User changeTo = new User(null, this.USER_NAME, newpw, null);

    // Perform patch
    given().header(this.authHeaderAdmin)
        .contentType(MEDIA_TYPE)
        .body(UserSerializationHelper.serialize(changeTo))
        .when()
        .patch(this.userUri)
        .then()
        .statusCode(200);

    // Try to login with new pw

    given().body(new UserCredentials(this.USER_NAME, newpw), ObjectMapperType.JACKSON_2)
        .contentType("application/json")
        .when()
        .post(BASE_URI + "tokens")
        .then()
        .statusCode(200)
        .body("data.attributes", hasKey("token"));

  }


  @Test
  void changeName() throws IOException {

    final String newname = "newname";
    final User changeTo = new User(null, newname, this.USER_PW, null);

    // Perform patch
    given().header(this.authHeaderAdmin)
        .contentType(MEDIA_TYPE)
        .body(UserSerializationHelper.serialize(changeTo))
        .when()
        .patch(this.userUri)
        .then()
        .statusCode(200);

    // Try to login with new name
    given().body(new UserCredentials(newname, this.USER_PW), ObjectMapperType.JACKSON_2)
        .contentType("application/json")
        .when()
        .post(BASE_URI + "tokens")
        .then()
        .statusCode(200)
        .body("data.attributes", hasKey("token"));

  }

  @Test
  void updateAsNormie() throws IOException {
    final String newname = "newname";
    final User changeTo = new User(null, newname, this.USER_PW, null);

    // Perform patch
    given().header(this.authHeaderNormie)
        .contentType(MEDIA_TYPE)
        .body(UserSerializationHelper.serialize(changeTo))
        .when()
        .patch(this.userUri)
        .then()
        .statusCode(401);

  }

  @Test
  void addRole() throws IOException {
    final List<String> adminRole = new ArrayList<>(Arrays.asList("admin"));
    final User changeTo = new User(null, this.USER_NAME, this.USER_PW, adminRole);

    // Perform patch
    final User updated = given().header(this.authHeaderAdmin)
        .contentType(MEDIA_TYPE)
        .body(UserSerializationHelper.serialize(changeTo))
        .when()
        .patch(this.userUri)
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(User.class, new JsonAPIMapper<>(User.class));

    assertTrue(updated.getRoles().stream().map(r -> r).anyMatch(d -> d.contentEquals("admin")));
  }



  @Test
  void updateId() throws IOException {
    final User changeTo = new User("someid", this.USER_NAME, this.USER_PW, null);

    // Perform patch
    given().header(this.authHeaderAdmin)
        .contentType(MEDIA_TYPE)
        .body(UserSerializationHelper.serialize(changeTo))
        .when()
        .patch(this.userUri)
        .then()
        .statusCode(400);
  }

}
