package net.explorviz.security.server;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.is;

import io.restassured.http.Header;
import net.explorviz.security.server.helper.AuthorizationHelper;
import net.explorviz.security.server.helper.JsonAPIListMapper;
import net.explorviz.shared.security.model.roles.Role;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoleTests {

  private static final String ROLE_URL = "http://localhost:8082/v1/roles";

  // Currently only two roles exist, nameley 'user' and 'admin'
  // It's not possible to add more roles

  private static String adminToken;
  private static String normieToken;

  private Header authHeaderAdmin;
  private Header authHeaderNormie;



  /**
   * Retrieves token for both an admin and an unprivileged user ("normie").
   * The default admin is used for the former, a normie is created.
   *
   * @throws IOException if serialization fails
   */
  @BeforeAll static void setUpAll() throws IOException {
    adminToken = AuthorizationHelper.getAdminToken();
    normieToken = AuthorizationHelper.getNormieToken();
  }

  @BeforeEach void setUp() {
    this.authHeaderAdmin = new Header("authorization", "Bearer " + adminToken);
    this.authHeaderNormie = new Header("authorization", "Bearer " + normieToken);
  }


  @Test
  @DisplayName("Get all roles")
  @SuppressWarnings("unchecked")
  public void getAll() {
    List<Role> actualRoles = new ArrayList<>(Arrays.asList(new Role("admin"),
        new Role("user")));

    List<Role> retrieved = given()
        .header(authHeaderAdmin)
        .when()
        .get(ROLE_URL)
        .then()
        .statusCode(200)
        .body("data.size()", is(2))
        .extract()
        .body()
        .as(List.class, new JsonAPIListMapper<Role>(Role.class));

    Assert.assertEquals(actualRoles, retrieved);
  }

  @Test
  @DisplayName("Get all roles without privileges")
  public void getAllAsNormie() {
    given()
        .header(authHeaderNormie)
        .when()
        .get(ROLE_URL)
        .then()
        .statusCode(403);
  }

}
