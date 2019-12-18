package net.explorviz.settings.server.resources.test;

import static io.restassured.RestAssured.given;
import io.restassured.http.Header;
import java.io.IOException;
import java.util.List;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.settings.server.resources.test.helper.DefaultSettings;
import net.explorviz.settings.server.resources.test.helper.JsonAPIListMapper;
import net.explorviz.settings.server.resources.test.helper.JsonAPIMapper;
import net.explorviz.settings.server.resources.test.helper.UsersHelper;
import net.explorviz.security.user.User;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PreferenceRetrieval {

  private static final String USER_PREF_URL =
          "http://localhost:8090/v1/preferences?filter[user]={uid}";
  private static final String PREF_URL = "http://localhost:8090/v1/preferences";

  private static String adminToken;
  private static String normieToken;

  private Header authHeaderAdmin;
  private Header authHeaderNormie;

  private static final String MEDIA_TYPE = "application/vnd.api+json";


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
    this.authHeaderAdmin = new Header("authorization", "Bearer " + adminToken);
    this.authHeaderNormie = new Header("authorization", "Bearer " + normieToken);
  }

  private void setPref(final String uid, final String settingId, final Object value) {
    final UserPreference up = new UserPreference(null, uid, settingId, value);

    given().header(this.authHeaderAdmin)
        .contentType(MEDIA_TYPE)
        .body(up, new JsonAPIMapper<>(UserPreference.class))
        .when()
        .post(PREF_URL);
  }


  @Test
  @SuppressWarnings("unchecked")
  void getOwnPrefs() {
    final User testUser = UsersHelper.getInstance()
        .createUser("tester", "test", null)
        .orElseThrow(IllegalStateException::new);

    final String settingId = DefaultSettings.keepHighlightingOnOpenOrClose.getId();
    final Boolean val = !DefaultSettings.keepHighlightingOnOpenOrClose.getDefaultValue();
    this.setPref(testUser.getId(), settingId, val);

    final String myToken = AuthorizationHelper.login("tester", "test")
        .orElseThrow(IllegalStateException::new)
        .getToken();
    final Header auth = new Header("authorization", "Bearer " + myToken);

    final List<UserPreference> prefs = given().header(auth)
        .when()
        .get(USER_PREF_URL.replace("{uid}", testUser.getId()))
        .then()
        .statusCode(200)
        .body("data.size()", CoreMatchers.is(1))
        .extract()
        .body()
        .as(List.class, new JsonAPIListMapper<>(UserPreference.class));

    final UserPreference pref = prefs.get(0);

    Assert.assertEquals(settingId, pref.getSettingId());
    Assert.assertEquals(testUser.getId(), pref.getUserId());
    Assert.assertEquals(val, pref.getValue());

    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }

  @Test
  void getPrefsOfOtherUser() {
    final User testUser = UsersHelper.getInstance()
        .createUser("tester", "test", null)
        .orElseThrow(IllegalStateException::new);


    final String user1token = AuthorizationHelper.login("tester", "test")
        .orElseThrow(IllegalStateException::new)
        .getToken();
    final Header auth = new Header("authorization", "Bearer " + user1token);


    given().header(this.authHeaderNormie)
        .when()
        .get(USER_PREF_URL.replace("{uid}", testUser.getId()))
        .then()
        .statusCode(403);

    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }

  @Test
  void getPrefsOfOtherAsAdmin() {
    final User testUser = UsersHelper.getInstance()
        .createUser("tester", "test", null)
        .orElseThrow(IllegalStateException::new);

    final String settingId = DefaultSettings.keepHighlightingOnOpenOrClose.getId();
    final Boolean val = !DefaultSettings.keepHighlightingOnOpenOrClose.getDefaultValue();
    this.setPref(testUser.getId(), settingId, val);

    given().header(this.authHeaderAdmin)
        .when()
        .get(USER_PREF_URL.replace("{uid}", testUser.getId()))
        .then()
        .statusCode(200)
        .body("data.size()", CoreMatchers.is(1));


    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }

}

