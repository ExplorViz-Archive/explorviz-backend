package net.explorviz.security.server;

import io.restassured.http.Header;
import net.explorviz.security.model.UserBatchRequest;
import net.explorviz.security.server.helper.AuthorizationHelper;
import net.explorviz.security.server.helper.JsonAPIListMapper;
import net.explorviz.security.server.helper.JsonAPIMapper;
import net.explorviz.security.server.helper.UsersHelper;
import net.explorviz.security.server.resources.BatchRequestSubResource;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class BatchRequest {

  private static final String BATCH_URL = "http://localhost:8082/v1/users/batch";
  private static final String USER_URL = "http://localhost:8082/v1/users/";
  private static final String PREF_URL
      = "http://localhost:8087/v1/users/{uid}/settings/preferences";

  private static String adminToken;
  private static String normieToken;

  private Header authHeaderAdmin;

  private static final String MEDIA_TYPE = "application/vnd.api+json";


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
  }

  @Test void createValid() {
    final int count = 10;
    final List<String> passwords = IntStream.range(0, count)
        .mapToObj(i -> RandomStringUtils.random(5, "abcdefghijklmnopqrstuvwxyz"))
        .collect(Collectors.toList());
    List<Role> roles = new ArrayList<>(Arrays.asList(new Role("user")));

    UserBatchRequest ubr =
        new UserBatchRequest("test", count, passwords, roles, null);

    UserBatchRequest retrieved = given()
        .header(authHeaderAdmin)
        .body(ubr, new JsonAPIMapper<UserBatchRequest>(UserBatchRequest.class))
        .contentType(MEDIA_TYPE)
        .when()
        .post(BATCH_URL)
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(UserBatchRequest.class, new JsonAPIMapper<UserBatchRequest>(UserBatchRequest.class));

    Assertions.assertEquals(count, retrieved.getUsers().size());


    // Delete the just created users
    retrieved.getUsers().stream()
        .map(User::getId)
        .forEach(i -> UsersHelper.getInstance().deleteUserById(i));
  }

  @Test
  @SuppressWarnings("unchecked")
  void filterByBatchId() {
    final int count = 10;
    final List<String> passwords = IntStream.range(0, count)
        .mapToObj(i -> RandomStringUtils.random(5, "abcdefghijklmnopqrstuvwxyz"))
        .collect(Collectors.toList());
    List<Role> roles = new ArrayList<>(Arrays.asList(new Role("user")));

    UserBatchRequest ubr =
        new UserBatchRequest("test", count, passwords, roles, null);

    // Create the batch
    String bid =
    given()
        .header(authHeaderAdmin)
        .body(ubr, new JsonAPIMapper<UserBatchRequest>(UserBatchRequest.class))
        .contentType(MEDIA_TYPE)
        .when()
        .post(BATCH_URL)
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(UserBatchRequest.class, new JsonAPIMapper<UserBatchRequest>(UserBatchRequest.class))
        .getUsers().get(0).getBatchId();

    // Get all users with the batch id
    List<User> retrieved = given()
        .contentType(MEDIA_TYPE)
        .header(authHeaderAdmin)
        .params("filter[batchid]", bid)
        .when()
        .get(USER_URL)
        .then()
        .statusCode(200)
        .body("data.size()", is(count))
        .extract().body().as(List.class, new JsonAPIListMapper<User>(User.class));

    // Delete the just created users
    retrieved.stream()
        .map(User::getId)
        .forEach(i -> UsersHelper.getInstance().deleteUserById(i));
  }

  @SuppressWarnings("unchecked")
  @Test void validWithPrefs() {
    final int count = 10;
    final List<String> passwords = IntStream.range(0, count)
        .mapToObj(i -> RandomStringUtils.random(5, "abcdefghijklmnopqrstuvwxyz"))
        .collect(Collectors.toList());
    List<Role> roles = new ArrayList<>(Arrays.asList(new Role("user")));

    float appVizTransparencyIntensity = 0.5f;
    boolean showFpsCounter = true;
    Map<String, Object> prefs = new HashMap<>();
    prefs.put("showFpsCounter", showFpsCounter);
    prefs.put("appVizTransparencyIntensity", appVizTransparencyIntensity);
    UserBatchRequest ubr =
        new UserBatchRequest("test", count, passwords, roles, prefs);

    // Create batch and retrieve the ids of the created users
    List<String> retrievedUids = given()
        .header(authHeaderAdmin)
        .body(ubr, new JsonAPIMapper<UserBatchRequest>(UserBatchRequest.class))
        .contentType(MEDIA_TYPE)
        .when()
        .post(BATCH_URL)
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(UserBatchRequest.class, new JsonAPIMapper<UserBatchRequest>(UserBatchRequest.class))
        .getUsers()
        .stream()
        .map(User::getId).collect(Collectors.toList());

    // Get Preferences for each user
    for (String uid: retrievedUids) {
      final String url = PREF_URL.replace("{uid}", uid);

      List<UserPreference> retrievedPrefs = given()
          .header(authHeaderAdmin)
          .when()
          .get(url)
          .then()
          .statusCode(200)
          .extract()
          .body()
          .as(List.class, new JsonAPIListMapper<UserPreference>(UserPreference.class));

      // Test if both prefs are present and the values are correct
      double appVizPref = (double) retrievedPrefs.stream()
          .filter(p -> p.getSettingId().contentEquals("appVizTransparencyIntensity"))
          .findAny()
          .get()
          .getValue();

      boolean fpsPref = (boolean) retrievedPrefs.stream()
          .filter(p -> p.getSettingId().contentEquals("showFpsCounter"))
          .findAny()
          .get()
          .getValue();

      Assertions.assertEquals(showFpsCounter, fpsPref);
      Assertions.assertEquals(appVizTransparencyIntensity, appVizPref);
    }
    // Delete the just created users
    retrievedUids.forEach(i -> UsersHelper.getInstance().deleteUserById(i));
  }


  @Test
  void invalidPasswordsLength() {
    final int count = 10;
    final List<String> passwords = IntStream.range(0, count-1)
        .mapToObj(i -> RandomStringUtils.random(5, "abcdefghijklmnopqrstuvwxyz"))
        .collect(Collectors.toList());
    List<Role> roles = new ArrayList<>(Arrays.asList(new Role("user")));

    UserBatchRequest ubr =
        new UserBatchRequest("test", count, passwords, roles, null);

    given()
        .header(authHeaderAdmin)
        .body(ubr, new JsonAPIMapper<UserBatchRequest>(UserBatchRequest.class))
        .contentType(MEDIA_TYPE)
        .when()
        .post(BATCH_URL)
        .then()
        .statusCode(400);

    // TODO: How to test that no users were actually created?
  }


  @Test
  void countLimit() {
    final int count = BatchRequestSubResource.MAX_COUNT+1;
    final List<String> passwords = IntStream.range(0, count)
        .mapToObj(i -> RandomStringUtils.random(5, "abcdefghijklmnopqrstuvwxyz"))
        .collect(Collectors.toList());
    List<Role> roles = new ArrayList<>(Arrays.asList(new Role("user")));

    UserBatchRequest ubr =
        new UserBatchRequest("test", count, passwords, roles, null);

    given()
        .header(authHeaderAdmin)
        .body(ubr, new JsonAPIMapper<UserBatchRequest>(UserBatchRequest.class))
        .contentType(MEDIA_TYPE)
        .when()
        .post(BATCH_URL)
        .then()
        .statusCode(400);

    // TODO: How to test that no users were actually created?
  }


  @Test
  void createExistingUser(){
    final String prefix = "test";
    final int count = 5;
    Optional<User> u = UsersHelper.getInstance().createUser(prefix+"-3", "pass", null);
    if (!u.isPresent()) {
      Assertions.fail();
    }
    final List<String> passwords = IntStream.range(0, count)
        .mapToObj(i -> RandomStringUtils.random(5, "abcdefghijklmnopqrstuvwxyz"))
        .collect(Collectors.toList());
    List<Role> roles = new ArrayList<>(Arrays.asList(new Role("user")));

    UserBatchRequest ubr =
        new UserBatchRequest("test", count, passwords, roles, null);

    given()
        .header(authHeaderAdmin)
        .body(ubr, new JsonAPIMapper<UserBatchRequest>(UserBatchRequest.class))
        .contentType(MEDIA_TYPE)
        .when()
        .post(BATCH_URL)
        .then()
        .statusCode(400);

    // TODO: How to test that no users were actually created?

    UsersHelper.getInstance().deleteUserById(u.get().getId());

  }




}
