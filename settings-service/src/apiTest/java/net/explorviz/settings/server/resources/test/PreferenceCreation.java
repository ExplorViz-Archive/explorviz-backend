package net.explorviz.settings.server.resources.test;

import static io.restassured.RestAssured.given;

import io.restassured.http.Header;
import java.io.IOException;
import net.explorviz.security.user.User;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.settings.server.resources.test.helper.DefaultSettings;
import net.explorviz.settings.server.resources.test.helper.JsonApiMapper;
import net.explorviz.settings.server.resources.test.helper.UsersHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// CHECKSTYLE.OFF: MagicNumberCheck
// CHECKSTYLE.OFF: MultipleStringLiteralsCheck


/**
 * Tests the creation of preferences.
 */
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert", "PMD.AvoidDuplicateLiterals"})
public class PreferenceCreation {

  // private static final String USER_PREF_URL =
  // "http://localhost:8090/v1/users/{uid}/settings/preferences";
  private static final String PREF_URL = "http://localhost:8090/v1/preferences";
  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private static String normieToken;

  // private Header authHeaderAdmin;
  private Header authHeaderNormie;




  /**
   * Retrieves token for both an admin and an unprivileged user ("normie"). The default admin is
   * used for the former, a normie is created.
   *
   * @throws IOException if serialization fails
   */
  @BeforeAll
  public static void setUpAll() throws IOException {
    normieToken = AuthorizationHelper.getNormieToken();
  }

  @BeforeEach
  public void setUp() {
    this.authHeaderNormie = new Header("authorization", "Bearer " + normieToken);
  }

  @Test
  public void createForSelf() {
    final User testUser = UsersHelper.getInstance()
        .createUser("tester", "test", null)
        .orElseThrow(IllegalStateException::new);

    final String settingId = DefaultSettings.APP_VIZ_COMM_ARROW_SIZE.getId();
    final double val = 2.0; // Default = 1.0, (0, 5)
    final UserPreference toCreate = new UserPreference(null, testUser.getId(), settingId, val);

    final String myToken = AuthorizationHelper.login("tester", "test")
        .orElseThrow(IllegalStateException::new)
        .getToken();
    final Header auth = new Header("authorization", "Bearer " + myToken);

    given().header(auth)
        .contentType(MEDIA_TYPE)
        .body(toCreate, new JsonApiMapper<>(UserPreference.class))
        .when()
        .post(PREF_URL)
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(UserPreference.class, new JsonApiMapper<>(UserPreference.class));

    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }

  @Test
  public void createForOther() {
    final User testUser = UsersHelper.getInstance()
        .createUser("tester", "test", null)
        .orElseThrow(IllegalStateException::new);

    final String settingId = DefaultSettings.APP_VIZ_COMM_ARROW_SIZE.getId();
    final double val = 2.0; // Default = 1.0, (0, 5)
    final UserPreference toCreate = new UserPreference(null, testUser.getId(), settingId, val);

    given().header(this.authHeaderNormie)
        .contentType(MEDIA_TYPE)
        .body(toCreate, new JsonApiMapper<>(UserPreference.class))
        .when()
        .post(PREF_URL)
        .then()
        .statusCode(403);
    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }

  @Test
  public void createWithInvalidValue() {
    final User testUser = UsersHelper.getInstance()
        .createUser("tester", "test", null)
        .orElseThrow(IllegalStateException::new);

    final String settingId = DefaultSettings.APP_VIZ_COMM_ARROW_SIZE.getId();
    final double val = DefaultSettings.APP_VIZ_COMM_ARROW_SIZE.getMax() + 1;
    final UserPreference toCreate = new UserPreference(null, testUser.getId(), settingId, val);

    final String myToken = AuthorizationHelper.login("tester", "test")
        .orElseThrow(IllegalStateException::new)
        .getToken();
    final Header auth = new Header("authorization", "Bearer " + myToken);

    given().header(auth)
        .contentType(MEDIA_TYPE)
        .body(toCreate, new JsonApiMapper<>(UserPreference.class))
        .when()
        .post(PREF_URL)
        .then()
        .statusCode(400);
    UsersHelper.getInstance().deleteUserById(testUser.getId());
  }


}
