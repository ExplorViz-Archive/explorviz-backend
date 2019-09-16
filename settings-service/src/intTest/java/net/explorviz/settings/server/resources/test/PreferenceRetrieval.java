package net.explorviz.settings.server.resources.test;

import io.restassured.http.Header;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.server.resources.test.helper.*;
import net.explorviz.shared.security.model.User;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;

public class PreferenceRetrieval {

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

  private void setPref(String uid, String settingId, Object value) {
    UserPreference up = new UserPreference(null, uid, settingId, value);

    given()
      .header(authHeaderAdmin)
      .contentType(MEDIA_TYPE)
      .body(up, new JsonAPIMapper<UserPreference>(UserPreference.class))
      .when()
      .post(PREF_URL);
  }


  @Test
  @SuppressWarnings("unchecked")
  void getOwnPrefs(){
    User testUser = UsersHelper.getInstance()
      .createUser("tester", "test", null).orElseThrow(IllegalStateException::new);

    String settingId = DefaultSettings.keepHighlightingOnOpenOrClose.getId();
    Boolean val = !DefaultSettings.keepHighlightingOnOpenOrClose.getDefaultValue();
    setPref(testUser.getId(), settingId, val);

    String myToken = AuthorizationHelper.login("tester", "test")
      .orElseThrow(IllegalStateException::new).getToken();
    Header auth = new Header("authorization", "Bearer " + myToken);

    List<UserPreference> prefs = given()
      .header(auth)
      .when()
      .get(USER_PREF_URL.replace("{uid}", testUser.getId()))
      .then()
      .statusCode(200)
      .body("data.size()", CoreMatchers.is(1))
      .extract().body().as(List.class, new JsonAPIListMapper<UserPreference>(UserPreference.class));

    UserPreference pref = prefs.get(0);

    Assert.assertEquals(settingId, pref.getSettingId());
    Assert.assertEquals(testUser.getId(), pref.getUserId());
    Assert.assertEquals(val, pref.getValue());

    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }

  @Test
  void getPrefsOfOtherUser(){
    User testUser = UsersHelper.getInstance()
      .createUser("tester", "test", null).orElseThrow(IllegalStateException::new);


    String user1token = AuthorizationHelper.login("tester", "test")
      .orElseThrow(IllegalStateException::new).getToken();
    Header auth = new Header("authorization", "Bearer " + user1token);


    given()
      .header(authHeaderNormie)
      .when()
      .get(USER_PREF_URL.replace("{uid}", testUser.getId()))
      .then()
      .statusCode(403);

    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }

  @Test
  void getPrefsOfOtherAsAdmin(){
    User testUser = UsersHelper.getInstance()
      .createUser("tester", "test", null).orElseThrow(IllegalStateException::new);

    String settingId = DefaultSettings.keepHighlightingOnOpenOrClose.getId();
    Boolean val = !DefaultSettings.keepHighlightingOnOpenOrClose.getDefaultValue();
    setPref(testUser.getId(), settingId, val);

   given()
      .header(authHeaderAdmin)
      .when()
      .get(USER_PREF_URL.replace("{uid}", testUser.getId()))
      .then()
      .statusCode(200)
      .body("data.size()", CoreMatchers.is(1));


    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }

}

