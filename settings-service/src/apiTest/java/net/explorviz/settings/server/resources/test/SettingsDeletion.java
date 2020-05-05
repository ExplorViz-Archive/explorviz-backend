package net.explorviz.settings.server.resources.test;

import static io.restassured.RestAssured.given;

import io.restassured.http.Header;
import java.io.IOException;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.settings.server.resources.test.helper.DefaultSettings;
import net.explorviz.settings.server.resources.test.helper.JsonApiMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// CHECKSTYLE.OFF: MagicNumberCheck
// CHECKSTYLE.OFF: MultipleStringLiteralsCheck


/**
 * Tests settings deletion.
 */
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert", "PMD.AvoidDuplicateLiterals"})
class SettingsDeletion {

  private static final String SETTINGS_URL = "http://localhost:8090/v1/settings";
  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private static String adminToken;
  private static String normieToken;

  private Header authHeaderAdmin;
  private Header authHeaderNormie;




  /**
   * Retrieves token for both an admin and an unprivileged user ("normie"). The default admin is
   * used for the former, a normie is created.
   *
   * @throws IOException if serialization fails
   */
  @BeforeAll
  public static void setUpAll() throws IOException {
    adminToken = AuthorizationHelper.getAdminToken();
    normieToken = AuthorizationHelper.getNormieToken();
  }

  @BeforeEach
  public void setUp() {
    this.authHeaderAdmin = new Header("authorization", "Bearer " + adminToken);
    this.authHeaderNormie = new Header("authorization", "Bearer " + normieToken);
  }

  private Setting create() {
    final Setting toCreate =
        new FlagSetting("testname", "a test setting", DefaultSettings.ORIGIN, false);

    final Setting created = given().header(this.authHeaderAdmin)
        .contentType(MEDIA_TYPE)
        .body(toCreate, new JsonApiMapper<>(Setting.class))
        .when()
        .post(SETTINGS_URL)
        .as(Setting.class, new JsonApiMapper<>(Setting.class));
    return created;
  }

  @Test
  public void deleteAsAdmin() {
    final Setting toDelete = this.create();

    given().header(this.authHeaderAdmin)
        .contentType(MEDIA_TYPE)
        .when()
        .delete(SETTINGS_URL + "/" + toDelete.getId())
        .then()
        .statusCode(204);
  }

  @Test
  public void deleteAsNormie() {
    given().header(this.authHeaderNormie)
        .contentType(MEDIA_TYPE)
        .when()
        .delete(SETTINGS_URL + "/sampleid")
        .then()
        .statusCode(401);
  }


}
