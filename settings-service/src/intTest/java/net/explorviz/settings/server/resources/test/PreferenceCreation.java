package net.explorviz.settings.server.resources.test;

import io.restassured.http.Header;
import java.io.IOException;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.settings.server.resources.test.helper.DefaultSettings;
import net.explorviz.settings.server.resources.test.helper.JsonAPIMapper;
import net.explorviz.settings.server.resources.test.helper.UsersHelper;
import net.explorviz.shared.security.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class PreferenceCreation {

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

  @Test
  void createForSelf() {
    User testUser = UsersHelper.getInstance()
        .createUser("tester", "test", null).orElseThrow(IllegalStateException::new);

    String settingId = DefaultSettings.appVizCommArrowSize.getId();
    double val = 2.0; // Default = 1.0, (0, 5)
    UserPreference toCreate = new UserPreference(null, testUser.getId(), settingId, val);

    String myToken = AuthorizationHelper.login("tester", "test")
        .orElseThrow(IllegalStateException::new).getToken();
    Header auth = new Header("authorization", "Bearer " + myToken);

    UserPreference created = given()
        .header(auth)
        .contentType(MEDIA_TYPE)
        .body(toCreate, new JsonAPIMapper<UserPreference>(UserPreference.class))
        .when()
        .post(PREF_URL)
        .then()
        .statusCode(200)
        .extract().body().as(UserPreference.class, new JsonAPIMapper<UserPreference>(UserPreference.class));
  }

  @Test
  void createForOther() {
    User testUser = UsersHelper.getInstance()
        .createUser("tester", "test", null).orElseThrow(IllegalStateException::new);

    String settingId = DefaultSettings.appVizCommArrowSize.getId();
    double val = 2.0; // Default = 1.0, (0, 5)
    UserPreference toCreate = new UserPreference(null, testUser.getId(), settingId, val);

    given()
        .header(authHeaderNormie)
        .contentType(MEDIA_TYPE)
        .body(toCreate, new JsonAPIMapper<UserPreference>(UserPreference.class))
        .when()
        .post(PREF_URL)
        .then()
        .statusCode(403);
  }

  @Test
  void createWithInvalidValue() {
    User testUser = UsersHelper.getInstance()
        .createUser("tester", "test", null).orElseThrow(IllegalStateException::new);

    String settingId = DefaultSettings.appVizCommArrowSize.getId();
    double val = DefaultSettings.appVizCommArrowSize.getMax()+1;
    UserPreference toCreate = new UserPreference(null, testUser.getId(), settingId, val);

    String myToken = AuthorizationHelper.login("tester", "test")
        .orElseThrow(IllegalStateException::new).getToken();
    Header auth = new Header("authorization", "Bearer " + myToken);

    given()
        .header(auth)
        .contentType(MEDIA_TYPE)
        .body(toCreate, new JsonAPIMapper<UserPreference>(UserPreference.class))
        .when()
        .post(PREF_URL)
        .then()
        .statusCode(400);
  }


}
