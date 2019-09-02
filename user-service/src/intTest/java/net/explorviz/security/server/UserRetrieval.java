package net.explorviz.security.server;

import io.restassured.http.Header;
import net.explorviz.security.server.helper.AuthorizationHelper;
import net.explorviz.security.server.helper.JsonAPIListMapper;
import net.explorviz.security.server.helper.UsersHelper;
import net.explorviz.security.services.exceptions.UserCrudException;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserRetrieval {

  private final static String BASE_URI = "http://localhost:8082/v1/";

  private static final String MEDIA_TYPE = "application/vnd.api+json";


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

  private List<User> createUsers(int count, String prefix, String password, List<Role> roles) {
    List<User> users = new ArrayList<>();
    for (int i=0; i<count; i++) {
      Optional<User> created = UsersHelper.getInstance()
          .createUser(prefix+i, password, roles);
      created.ifPresent(users::add);
    }
    return users;
  }

  private void deleteUsers(List<User> users) {
    users.forEach(u -> UsersHelper.getInstance().deleteUserById(u.getId()));
  }

  @AfterEach
  public void tearDown() throws UserCrudException {
    // Delete all users but the admin

  }

  @Test
  @DisplayName("Get all users in the database")
  public void getAllAsAdmin() throws UserCrudException {
    List<User> created = createUsers(5, "test", "pw", null);
    // All users must be >= 1
    given()
        .contentType(MEDIA_TYPE)
        .header(authHeaderAdmin)
        .when()
        .get(BASE_URI+"users")
        .then()
        .statusCode(200)
        .body("$", hasKey("data"))
        .body("data.size()", greaterThan(5));
    deleteUsers(created);
  }

  @Test
  @DisplayName("Get all users in the database as normie")
  public void getAllAsNormie()  {
    // Should return 403 status code
    given()
        .contentType(MEDIA_TYPE)
        .header(authHeaderNormie)
        .when()
        .get(BASE_URI+"users")
        .then()
        .statusCode(403);
  }


  @Test
  @DisplayName("Get user by id as admin")
  public void getSelfAsAdmin() {
    String id = AuthorizationHelper.getAdmin().getId();
    given()
        .contentType(MEDIA_TYPE)
        .header(authHeaderAdmin)
        .when()
        .get(BASE_URI+"users/"+id)
        .then()
        .statusCode(200)
        .body("$", hasKey("data"))
        .body("data.id", is(id));
  }

  @Test
  @DisplayName("Get user by id as normie")
  public void getUserAsNormie() {
    String id = AuthorizationHelper.getNormie().getId();
    given()
        .contentType(MEDIA_TYPE)
        .header(authHeaderNormie)
        .when()
        .get(BASE_URI+"users/"+id)
        .then()
        .statusCode(403);
  }

  @Test
  @DisplayName("Pagination: First Page")
  public void paginationFirstPage() {
    List<User> users = createUsers(5, "test", "pw", null);
    int size = 2;
    int num = 0;
    given()
        .contentType(MEDIA_TYPE)
        .header(authHeaderAdmin)
        .params("page[size]", size,
            "page[number]", num)
        .when()
        .get(BASE_URI+"users/")
        .then()
        .statusCode(200)
        .body("data.size()", is(size))
        .body("links", hasKey("first"))
        .body("links", hasKey("next"))
        .body("links", hasKey("last"))
        .body("links", not(hasKey("prev")));
    deleteUsers(users);
  }

  @Test
  @DisplayName("Pagination: Last Page")
  public void paginationLastPage() {
    List<User> users = createUsers(5, "test", "pw", null);
    int size = 1;
    int num = UsersHelper.getInstance().count()-1;
    given()
        .contentType(MEDIA_TYPE)
        .header(authHeaderAdmin)
        .params("page[size]", size,
            "page[number]", num)
        .when()
        .get(BASE_URI+"users/")
        .then()
        .statusCode(200)
        .body("data.size()", is(size))
        .body("links", hasKey("first"))
        .body("links", hasKey("prev"))
        .body("links", hasKey("last"))
        .body("links", not(hasKey("next")));
    deleteUsers(users);
  }

  @Test
  @DisplayName("Pagination: Empty Page")
  public void paginationEmptyPage() {
    List<User> users = createUsers(5, "test", "pw", null);
    int size = 1;
    int num = UsersHelper.getInstance().count();
    given()
        .contentType(MEDIA_TYPE)
        .header(authHeaderAdmin)
        .params("page[size]", size,
            "page[number]", num)
        .when()
        .get(BASE_URI+"users/")
        .then()
        .statusCode(200)
        .body("data.size()", is(0))
        .body("links", hasKey("first"))
        .body("links", hasKey("prev"))
        .body("links", hasKey("last"))
        .body("links", not(hasKey("next")));
    deleteUsers(users);
  }

  @Test
  @DisplayName("Pagination: Middle Page")
  public void paginationMiddlePage() {
    List<User> users = createUsers(5, "test", "pw", null);
    int size = 1;
    int num = UsersHelper.getInstance().count()/2;
    given()
        .contentType(MEDIA_TYPE)
        .header(authHeaderAdmin)
        .params("page[size]", size,
            "page[number]", num)
        .when()
        .get(BASE_URI+"users/")
        .then()
        .statusCode(200)
        .body("data.size()", is(size))
        .body("links", hasKey("first"))
        .body("links", hasKey("prev"))
        .body("links", hasKey("last"))
        .body("links", hasKey("next"));
    deleteUsers(users);
  }


  @Test
  @DisplayName("Filter by Role")
  @SuppressWarnings("unchecked")
  public void FilterByRole() {
    String adminRole = "admin";
    List<Role> roles = new ArrayList<>(Arrays.asList(new Role(adminRole)));
    List<User> users = createUsers(5, "test", "pw", roles);
    int size = 1;
    int num = UsersHelper.getInstance().count()/2;
    List<User> returned = given()
        .contentType(MEDIA_TYPE)
        .header(authHeaderAdmin)
        .params("filter[roles]", adminRole)
        .when()
        .get(BASE_URI+"users/")
        .then()
        .statusCode(200)
        .body("data.size()", greaterThan(5))
        .extract().body().as(List.class, new JsonAPIListMapper<User>(User.class));


    returned.forEach(u -> assertTrue(
        u.getRoles().stream().anyMatch(r -> r.getDescriptor().contentEquals(adminRole))));

    deleteUsers(users);
  }


  @Test
  @DisplayName("Filter by Role and Paginate")
  @SuppressWarnings("unchecked")
  public void FilterByRoleAndPaginate() {
    String adminRole = "admin";
    List<Role> roles = new ArrayList<>(Arrays.asList(new Role(adminRole)));
    List<User> users = createUsers(5, "test", "pw", roles);
    int size = 1;
    int num = UsersHelper.getInstance().count()/2;
    List<User> returned = given()
        .contentType(MEDIA_TYPE)
        .header(authHeaderAdmin)
        .params("filter[roles]", adminRole,
            "page[size]", size,
            "page[number]", num)
        .when()
        .get(BASE_URI+"users/")
        .then()
        .statusCode(200)
        .body("data.size()", is(size))
        .extract().body().as(List.class, new JsonAPIListMapper<User>(User.class));

    returned.forEach(u -> assertTrue(
        u.getRoles().stream().anyMatch(r -> r.getDescriptor().contentEquals(adminRole))));

    deleteUsers(users);
  }
}
