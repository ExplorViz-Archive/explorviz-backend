package net.explorviz.security.server.resources.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import io.restassured.http.Header;
import java.io.IOException;
import java.util.List;
import net.explorviz.security.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.security.server.resources.test.helper.JsonAPIListMapper;
import net.explorviz.security.user.Role;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RoleRetrieval {

  private static final String ROLE_URL = "http://localhost:8090/v1/roles";

  private static final int HTTP_OK_CODE = 200;
  private static final int HTTP_FORBIDDEN_CODE = 401;

  // Currently only two roles exist, namely 'user' and 'admin'
  // It's not possible to add more roles

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
    this.authHeaderAdmin = new Header("authorization", "Bearer " + adminToken); // NOCS
    this.authHeaderNormie = new Header("authorization", "Bearer " + normieToken);
  }


  @Test
  @DisplayName("Get all roles")
  @SuppressWarnings("unchecked")
  public void testGetAllRolesAsAdmin() {
    final List<Role> actualRoles = Role.ROLES;

    final List<Role> retrieved = given().header(this.authHeaderAdmin)
        .when()
        .get(ROLE_URL)
        .then()
        .statusCode(HTTP_OK_CODE)
        .body("data.size()", is(2))
        .extract()
        .body()
        .as(List.class, new JsonAPIListMapper<>(Role.class));

    Assert.assertEquals(actualRoles, retrieved);
  }

  @Test
  @DisplayName("Get all roles without privileges")
  public void testGetAllRolesAsNormie() { // NOPMD
    given().header(this.authHeaderNormie)
        .when()
        .get(ROLE_URL)
        .then()
        .statusCode(HTTP_FORBIDDEN_CODE);
  }

}
