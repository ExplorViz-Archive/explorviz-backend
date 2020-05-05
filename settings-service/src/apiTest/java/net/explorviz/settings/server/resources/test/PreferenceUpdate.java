package net.explorviz.settings.server.resources.test;

import static io.restassured.RestAssured.given;

import io.restassured.http.Header;
import net.explorviz.security.user.User;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.settings.server.resources.test.helper.DefaultSettings;
import net.explorviz.settings.server.resources.test.helper.JsonApiMapper;
import net.explorviz.settings.server.resources.test.helper.UsersHelper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// CHECKSTYLE.OFF: MagicNumberCheck
// CHECKSTYLE.OFF: MultipleStringLiteralsCheck


/**
 * Tests preference updates.
 */
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert", "PMD.AvoidDuplicateLiterals"})
class PreferenceUpdate {

  private static final String PREF_URL = "http://localhost:8090/v1/preferences";
  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private static String adminToken;
  private static String normieToken;

  private Header authHeaderAdmin;
  private Header authHeaderNormie;




  /**
   * Retrieves token for both an admin and an unprivileged user ("normie"). The default admin is
   * used for the former, a normie is created.
   *
   */
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

  private UserPreference createPref(final String uid, final String settingId, final Object value) {
    final UserPreference up = new UserPreference(null, uid, settingId, value);

    return given().header(this.authHeaderAdmin)
        .contentType(MEDIA_TYPE)
        .body(up, new JsonApiMapper<>(UserPreference.class))
        .when()
        .post(PREF_URL)
        .as(UserPreference.class, new JsonApiMapper<>(UserPreference.class));
  }

  @Test
  public void updateOwnPref() {
    final User testUser = UsersHelper.getInstance()
        .createUser("tester", "test", null)
        .orElseThrow(IllegalStateException::new);

    final String settingId = DefaultSettings.APP_VIZ_COMM_ARROW_SIZE.getId();
    final Double val = 2.0; // Default = 1.0, (0, 5)
    final UserPreference createdPref = this.createPref(testUser.getId(), settingId, val);

    final UserPreference toUpdate =
        new UserPreference(createdPref.getId(), testUser.getId(), settingId, val + 1);

    final String myToken = AuthorizationHelper.login("tester", "test")
        .orElseThrow(IllegalStateException::new)
        .getToken();
    final Header auth = new Header("authorization", "Bearer " + myToken);

    final UserPreference updated = given().header(auth)
        .contentType(MEDIA_TYPE)
        .body(toUpdate, new JsonApiMapper<>(UserPreference.class))
        .when()
        .patch(PREF_URL + "/" + createdPref.getId())
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(UserPreference.class, new JsonApiMapper<>(UserPreference.class));

    Assert.assertEquals(updated.getValue(), toUpdate.getValue());
    Assert.assertEquals(updated, toUpdate);

    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }

  @Test
  public void updateOwnPrefInvalidValue() {
    final User testUser = UsersHelper.getInstance()
        .createUser("tester", "test", null)
        .orElseThrow(IllegalStateException::new);

    final String settingId = DefaultSettings.APP_VIZ_COMM_ARROW_SIZE.getId();
    final Double val = 2.0; // Default = 1.0, (0, 5)
    final UserPreference createdPref = this.createPref(testUser.getId(), settingId, val);

    final UserPreference toUpdate = new UserPreference(createdPref.getId(), testUser.getId(),
        settingId, DefaultSettings.APP_VIZ_COMM_ARROW_SIZE.getMax() + 1);

    final String myToken = AuthorizationHelper.login("tester", "test")
        .orElseThrow(IllegalStateException::new)
        .getToken();
    final Header auth = new Header("authorization", "Bearer " + myToken);

    given().header(auth)
        .contentType(MEDIA_TYPE)
        .body(toUpdate, new JsonApiMapper<>(UserPreference.class))
        .when()
        .patch(PREF_URL + "/" + createdPref.getId())
        .then()
        .statusCode(400);

    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }

  @Test
  public void updatePrefsOfOtherUser() {
    final User testUser = UsersHelper.getInstance()
        .createUser("tester", "test", null)
        .orElseThrow(IllegalStateException::new);

    final String settingId = DefaultSettings.APP_VIZ_COMM_ARROW_SIZE.getId();
    final Double val = 2.0; // Default = 1.0, (0, 5)
    final UserPreference createdPref = this.createPref(testUser.getId(), settingId, val);

    final UserPreference toUpdate =
        new UserPreference(createdPref.getId(), testUser.getId(), settingId, val + 1);

    given().header(this.authHeaderNormie)
        .contentType(MEDIA_TYPE)
        .body(toUpdate, new JsonApiMapper<>(UserPreference.class))
        .when()
        .patch(PREF_URL + "/" + createdPref.getId())
        .then()
        .statusCode(403);

    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }
}
