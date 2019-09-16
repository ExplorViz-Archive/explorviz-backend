package net.explorviz.settings.server.resources.test;

import io.restassured.http.Header;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.settings.server.resources.test.helper.DefaultSettings;
import net.explorviz.settings.server.resources.test.helper.JsonAPIMapper;
import net.explorviz.settings.server.resources.test.helper.UsersHelper;
import net.explorviz.shared.security.model.User;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;

class PreferenceDeletion {

  private static final String USER_PREF_URL = "http://localhost:8087/v1/users/{uid}/settings/preferences";
  private static final String PREF_URL = "http://localhost:8087/v1/users/settings/preferences";

  private static String adminToken;
  private static String normieToken;

  private Header authHeaderAdmin;
  private Header authHeaderNormie;

  private static final String MEDIA_TYPE = "application/vnd.api+json";


  /**
   * Retrieves token for both an admin and an unprivileged user ("normie").
   * The default admin is used for the former, a normie is created.
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
    this.authHeaderAdmin = new Header("authorization", "Bearer " + adminToken);
    this.authHeaderNormie = new Header("authorization", "Bearer " + normieToken);
  }

  private UserPreference setPref(String uid, String settingId, Object value) {
    UserPreference up = new UserPreference(null, uid, settingId, value);

    return given()
      .header(authHeaderAdmin)
      .contentType(MEDIA_TYPE)
      .body(up, new JsonAPIMapper<UserPreference>(UserPreference.class))
      .when()
      .post(PREF_URL)
      .as(UserPreference.class, new JsonAPIMapper<UserPreference>(UserPreference.class));
  }



  @Test
  void deleteOwnPref(){
    User testUser = UsersHelper.getInstance()
      .createUser("tester", "test", null).orElseThrow(IllegalStateException::new);

    String settingId = DefaultSettings.keepHighlightingOnOpenOrClose.getId();
    Boolean val = !DefaultSettings.keepHighlightingOnOpenOrClose.getDefaultValue();
    UserPreference createdPref = setPref(testUser.getId(), settingId, val);

    String myToken = AuthorizationHelper.login("tester", "test")
      .orElseThrow(IllegalStateException::new).getToken();
    Header auth = new Header("authorization", "Bearer " + myToken);

    given()
      .header(auth)
      .contentType(MEDIA_TYPE)
      .when()
      .delete(PREF_URL+"/"+createdPref.getId())
      .then()
      .statusCode(204);

    given()
      .header(auth)
      .when()
      .get(USER_PREF_URL.replace("{uid}", testUser.getId()))
      .then()
      .statusCode(200)
      .body("data.size()", CoreMatchers.is(0));

    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }

  /**
   * All preference should be deleted if the corresponding user is removed
   */
  @Test
  void testDeletionByUserDeletion() throws InterruptedException {
    User testUser = UsersHelper.getInstance()
      .createUser("tester", "test", null).orElseThrow(IllegalStateException::new);

    String settingId = DefaultSettings.keepHighlightingOnOpenOrClose.getId();
    Boolean val = !DefaultSettings.keepHighlightingOnOpenOrClose.getDefaultValue();
    setPref(testUser.getId(), settingId, val);

    // Delete User
    UsersHelper.getInstance().deleteUserById(testUser.getId());

    // Wait until event handled
    Thread.sleep(500);

    given()
      .header(authHeaderAdmin)
      .when()
      .get(USER_PREF_URL.replace("{uid}", testUser.getId()))
      .then()
      .statusCode(200)
      .body("data.size()", CoreMatchers.is(0));

    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }


  @Test
  void deletePrefOfOtherUser(){
    User testUser = UsersHelper.getInstance()
      .createUser("tester", "test", null).orElseThrow(IllegalStateException::new);

    String settingId = DefaultSettings.keepHighlightingOnOpenOrClose.getId();
    Boolean val = !DefaultSettings.keepHighlightingOnOpenOrClose.getDefaultValue();
    String id = setPref(testUser.getId(), settingId, val).getId();

    given()
      .header(authHeaderNormie)
      .contentType(MEDIA_TYPE)
      .when()
      .delete(PREF_URL+"/"+id)
      .then()
      .statusCode(403);

    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }

  @Test
  void deletePrefOfOtherUserAsAdmin(){
    User testUser = UsersHelper.getInstance()
      .createUser("tester", "test", null).orElseThrow(IllegalStateException::new);

    String settingId = DefaultSettings.keepHighlightingOnOpenOrClose.getId();
    Boolean val = !DefaultSettings.keepHighlightingOnOpenOrClose.getDefaultValue();
    String id = setPref(testUser.getId(), settingId, val).getId();

    given()
      .header(authHeaderAdmin)
      .contentType(MEDIA_TYPE)
      .when()
      .delete(PREF_URL+"/"+id)
      .then()
      .statusCode(204);

    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }

}
