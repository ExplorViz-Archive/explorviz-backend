package net.explorviz.security.server;

import io.restassured.http.Header;
import net.explorviz.security.server.helper.AuthorizationHelper;
import net.explorviz.security.server.helper.UsersHelper;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class UserDeletion {

  private final static String BASE_URI = "http://localhost:8082/v1/";


  private static String adminToken;
  private static String normieToken;

  private Header authHeaderAdmin;
  private Header authHeaderNormie;

  @BeforeAll static void setUpAll() throws IOException {
    adminToken = AuthorizationHelper.getAdminToken();
    normieToken = AuthorizationHelper.getNormieToken();
  }

  @BeforeEach void setUp() {
    this.authHeaderAdmin = new Header("authorization", "Bearer " + adminToken);
    this.authHeaderNormie = new Header("authorization", "Bearer " + normieToken);
  }


  @Test
  @DisplayName("Delete a User") void deleteUser() {
    Optional<User> deleteMe
        = UsersHelper.getInstance().createUser("deleteme", "pw", null);

    if (!deleteMe.isPresent()) {
      Assertions.fail();
    }

    given()
        .header(authHeaderAdmin)
        .when()
        .delete(BASE_URI+"users/"+deleteMe.get().getId())
        .then()
        .statusCode(204);

    given()
        .header(authHeaderAdmin)
        .when()
        .get(BASE_URI+deleteMe.get().getId())
        .then()
        .statusCode(404);
  }

  @Test
  @DisplayName("Delete a User without privileges") void deleteUserAsNormie() {
    Optional<User> deleteMe
        = UsersHelper.getInstance().createUser("deleteme", "pw", null);

    if (!deleteMe.isPresent()) {
      Assertions.fail();
    }

    given()
        .header(authHeaderNormie)
        .when()
        .delete(BASE_URI+"users/"+deleteMe.get().getId())
        .then()
        .statusCode(403);

    UsersHelper.getInstance().deleteUserById(deleteMe.get().getId());
  }

  @Test
  @DisplayName("Delete last admin") void deleteLastAdmin() {

    // Check if there are other admins next to the default admin, and delete them
    UsersHelper.getInstance().getAll().stream()
        .filter(u -> u.getRoles().equals(new Role("admin")))
        .filter(u -> !u.getUsername().contentEquals("admin"))
        .map(User::getId)
        .forEach(i -> UsersHelper.getInstance().deleteUserById(i));

    String id = AuthorizationHelper.getAdmin().getId();

    given()
        .header(authHeaderAdmin)
        .when()
        .delete(BASE_URI+"users/"+id)
        .then()
        .statusCode(400);

  }

}
