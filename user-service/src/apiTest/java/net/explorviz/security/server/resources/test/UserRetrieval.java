package net.explorviz.security.server.resources.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.http.Header;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.explorviz.security.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.security.server.resources.test.helper.JsonApiListMapper;
import net.explorviz.security.server.resources.test.helper.StatusCodes;
import net.explorviz.security.server.resources.test.helper.UsersHelper;
import net.explorviz.security.services.exceptions.UserCrudException;
import net.explorviz.security.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// CHECKSTYLE.OFF: MagicNumberCheck
// CHECKSTYLE.OFF: MultipleStringLiteralsCheck

/**
 * Tests retrieval of users.
 */
public class UserRetrieval {

  private static final String BASE_URI = "http://localhost:8090/v1/";

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String LINKS_PATH = "links";
  private static final String PAGE_SIZE = "page[size]";
  private static final String PAGE_NUMBER = "page[number]";
  private static final String LINK_FIRST = "first";
  private static final String LINK_PREV = "prev";
  private static final String LINK_LAST = "last";
  private static final String LINK_NEXT = "next";
  private static final String FILTER_ROLES = "filter[roles]";


  private static String adminToken;
  private static String normieToken;
  private Header authHeaderAdmin;
  private Header authHeaderNormie;


  @BeforeAll
  public static void setUpAll() {
    adminToken = AuthorizationHelper.getAdminToken();
    normieToken = AuthorizationHelper.getNormieToken();
  }


  @BeforeEach
  public void setUp() {
    this.authHeaderAdmin = new Header("authorization", "Bearer " + adminToken);
    this.authHeaderNormie = new Header("authorization", "Bearer " + normieToken);
  }

  private List<User> createUsers(final int count, final String prefix, final String password,
                                 final List<String> roles) {
    final List<User> users = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      final Optional<User> created =
          UsersHelper.getInstance().createUser(prefix + i, password, roles);
      created.ifPresent(users::add);
    }
    return users;
  }

  private void deleteUsers(final List<User> users) {
    users.forEach(u -> UsersHelper.getInstance().deleteUserById(u.getId()));
  }

  @AfterEach
  public void tearDown() throws UserCrudException {
    // Delete all users but the admin

  }

  @Test
  @DisplayName("Get all users in the database")
  public void retrieveAllAsAdmin() throws UserCrudException {
    final List<User> created = this.createUsers(5, "test", "pw", null);
    // All users must be >= 1
    given().contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .when()
        .get(BASE_URI + "users")
        .then()
        .statusCode(StatusCodes.STATUS_OK)
        .body("$", hasKey("data"))
        .body("data.size()", greaterThan(5));
    this.deleteUsers(created);
  }

  @Test
  @DisplayName("Get all users in the database as normie")
  public void retrieveAllAsNormie() {
    // Should return 401 status code
    given().contentType(MEDIA_TYPE)
        .header(this.authHeaderNormie)
        .when()
        .get(BASE_URI + "users")
        .then()
        .statusCode(StatusCodes.STATUS_UNAUTHORIZED);
  }


  @Test
  @DisplayName("Get user by id as admin")
  public void retrieveSelfAsAdmin() {
    final String id = AuthorizationHelper.getAdmin().getId();
    given().contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .when()
        .get(BASE_URI + "users/" + id)
        .then()
        .statusCode(StatusCodes.STATUS_OK)
        .body("$", hasKey("data"))
        .body("data.id", is(id));
  }

  @Test
  @DisplayName("Get user by id as normie")
  public void retrieveUserAsNormie() {
    final String id = AuthorizationHelper.getNormie().getId();
    given().contentType(MEDIA_TYPE)
        .header(this.authHeaderNormie)
        .when()
        .get(BASE_URI + "users/" + id)
        .then()
        .statusCode(StatusCodes.STATUS_UNAUTHORIZED);
  }

  @Test
  @DisplayName("Pagination: First Page")
  public void paginationFirstPage() {
    final List<User> users = this.createUsers(5, "test", "pw", null);
    final int size = 2;
    final int num = 0;
    given().contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .params(PAGE_SIZE, size, PAGE_NUMBER, num)
        .when()
        .get(BASE_URI + "users/")
        .then()
        .statusCode(StatusCodes.STATUS_OK)
        .body("data.size()", is(size))
        .body(LINKS_PATH, hasKey(LINK_FIRST))
        .body(LINKS_PATH, hasKey(LINK_NEXT))
        .body(LINKS_PATH, hasKey(LINK_LAST))
        .body(LINKS_PATH, not(hasKey(LINK_PREV)));
    this.deleteUsers(users);
  }

  @Test
  @DisplayName("Pagination: Last Page")
  public void paginationLastPage() {
    final List<User> users = this.createUsers(5, "test", "pw", null);
    final int size = 1;
    final int num = UsersHelper.getInstance().count() - 1;
    given().contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .params(PAGE_SIZE, size, PAGE_NUMBER, num)
        .when()
        .get(BASE_URI + "users/")
        .then()
        .statusCode(StatusCodes.STATUS_OK)
        .body("data.size()", is(size))
        .body(LINKS_PATH, hasKey(LINK_FIRST))
        .body(LINKS_PATH, hasKey(LINK_PREV))
        .body(LINKS_PATH, hasKey(LINK_LAST))
        .body(LINKS_PATH, not(hasKey(LINK_NEXT)));
    this.deleteUsers(users);
  }

  @Test
  @DisplayName("Pagination: Empty Page")
  public void paginationEmptyPage() {
    final List<User> users = this.createUsers(5, "test", "pw", null);
    final int size = 1;
    final int num = UsersHelper.getInstance().count();
    given().contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .params(PAGE_SIZE, size, PAGE_NUMBER, num)
        .when()
        .get(BASE_URI + "users/")
        .then()
        .statusCode(StatusCodes.STATUS_OK)
        .body("data.size()", is(0))
        .body(LINKS_PATH, hasKey(LINK_FIRST))
        .body(LINKS_PATH, hasKey(LINK_PREV))
        .body(LINKS_PATH, hasKey(LINK_LAST))
        .body(LINKS_PATH, not(hasKey(LINK_NEXT)));
    this.deleteUsers(users);
  }

  @Test
  @DisplayName("Pagination: Middle Page")
  public void paginationMiddlePage() {
    final List<User> users = this.createUsers(5, "test", "pw", null);
    final int size = 1;
    final int num = UsersHelper.getInstance().count() / 2;
    given().contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .params(PAGE_SIZE, size, PAGE_NUMBER, num)
        .when()
        .get(BASE_URI + "users/")
        .then()
        .statusCode(StatusCodes.STATUS_OK)
        .body("data.size()", is(size))
        .body(LINKS_PATH, hasKey(LINK_FIRST))
        .body(LINKS_PATH, hasKey(LINK_PREV))
        .body(LINKS_PATH, hasKey(LINK_LAST))
        .body(LINKS_PATH, hasKey(LINK_NEXT));
    this.deleteUsers(users);
  }


  @Test
  @DisplayName("Filter by Role")
  @SuppressWarnings("unchecked")
  public void filterByRole() {
    final String adminRole = "admin";
    final List<String> roles = new ArrayList<>(Arrays.asList(adminRole));
    final List<User> users = this.createUsers(5, "test", "pw", roles);

    final List<User> returned = given().contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .params(FILTER_ROLES, adminRole)
        .when()
        .get(BASE_URI + "users/")
        .then()
        .statusCode(StatusCodes.STATUS_OK)
        .body("data.size()", greaterThan(5))
        .extract()
        .body()
        .as(List.class, new JsonApiListMapper<>(User.class));


    returned
        .forEach(u -> assertTrue(u.getRoles().stream().anyMatch(r -> r.contentEquals(adminRole))));

    this.deleteUsers(users);
  }


  @Test
  @DisplayName("Filter by Role and Paginate")
  @SuppressWarnings("unchecked")
  public void filterByRoleAndPaginate() {
    final String adminRole = "admin";
    final List<String> roles = new ArrayList<>(Arrays.asList(adminRole));
    final List<User> users = this.createUsers(5, "test", "pw", roles);
    final int size = 1;
    final int num = UsersHelper.getInstance().count() / 2;
    final List<User> returned = given().contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .params(FILTER_ROLES, adminRole, PAGE_SIZE, size, PAGE_NUMBER, num)
        .when()
        .get(BASE_URI + "users/")
        .then()
        .statusCode(StatusCodes.STATUS_OK)
        .body("data.size()", is(size))
        .extract()
        .body()
        .as(List.class, new JsonApiListMapper<>(User.class));

    returned
        .forEach(u -> assertTrue(u.getRoles().stream().anyMatch(r -> r.contentEquals(adminRole))));

    this.deleteUsers(users);
  }
}
