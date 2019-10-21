package net.explorviz.security.server.resources.test;

import static io.restassured.RestAssured.given;
import io.restassured.mapper.ObjectMapperType;
import net.explorviz.security.model.UserCredentials;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class Login {

  // This is bad since other test rely on this

  private static final String AUTH_URL = "http://localhost:8090/v1/tokens/";
  private static final String ADMIN_NAME = "admin";
  private static final String ADMIN_PW = "password";


  @Test
  void testValidLogin() {
    given().contentType("application/json")
        .body(new UserCredentials(ADMIN_NAME, ADMIN_PW), ObjectMapperType.JACKSON_2)
        .when()
        .post(AUTH_URL)
        .then()
        .statusCode(200)
        .body("data.attributes", Matchers.hasKey("token"));
  }


  @Test
  void testInvalidLogin() {
    given().contentType("application/json")
        .body(new UserCredentials(ADMIN_NAME, "invalidpw"), ObjectMapperType.JACKSON_2)
        .when()
        .post(AUTH_URL)
        .then()
        .statusCode(403);
  }



}
