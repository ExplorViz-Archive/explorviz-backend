package net.explorviz.settings.server.resources.test;

import io.restassured.http.Header;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.settings.server.resources.test.helper.DefaultSettings;
import net.explorviz.settings.server.resources.test.helper.JsonAPIMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;

class SettingsInfoDeletion {

  private static final String SETTINGS_URL = "http://localhost:8087/v1/settings/info";

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

  private Setting create() {
    Setting toCreate
            = new FlagSetting("testname", "a test setting",
            DefaultSettings.origin, false);

    Setting created = given()
      .header(authHeaderAdmin)
      .contentType(MEDIA_TYPE)
      .body(toCreate, new JsonAPIMapper<Setting>(Setting.class))
      .when()
      .post(SETTINGS_URL)
      .as(Setting.class, new JsonAPIMapper<Setting>(Setting.class));
    return created;
  }

  @Test
  void deleteAsAdmin() {
    Setting toDelete = create();

    given()
      .header(authHeaderAdmin)
      .contentType(MEDIA_TYPE)
      .when()
      .delete(SETTINGS_URL+"/"+toDelete.getId())
      .then()
      .statusCode(204);
  }

  @Test
  void deleteAsNormie() {
     given()
      .header(authHeaderNormie)
      .contentType(MEDIA_TYPE)
      .when()
      .delete(SETTINGS_URL+"/sampleid")
      .then()
      .statusCode(403);
  }


}
