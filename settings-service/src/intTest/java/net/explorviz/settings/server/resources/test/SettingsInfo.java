package net.explorviz.settings.server.resources.test;

import io.restassured.http.Header;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.settings.server.resources.test.helper.DefaultSettings;
import net.explorviz.settings.server.resources.test.helper.JsonAPIListMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;

public class SettingsInfo {

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
  @BeforeAll static void setUpAll() throws IOException {
    adminToken = AuthorizationHelper.getAdminToken();
    normieToken = AuthorizationHelper.getNormieToken();
  }

  @BeforeEach void setUp() {
    this.authHeaderAdmin = new Header("authorization", "Bearer " + adminToken);
    this.authHeaderNormie = new Header("authorization", "Bearer " + normieToken);
  }


  @Test
  @SuppressWarnings("unchecked")
  void getAllAsAdmin() {
    List<Setting> retrieved = given()
        .header(authHeaderNormie)
        .when()
        .get(SETTINGS_URL)
        .then()
        .statusCode(200)
        .body("$", hasKey("data"))
        .body("data.size()", is(DefaultSettings.all.size()))
        .extract().body().as(List.class, new JsonAPIListMapper<Setting>(Setting.class));
    List<String> ids = retrieved.stream().map(Setting::getId).collect(Collectors.toList());
    List<String> defaultIds =
        DefaultSettings.all.stream().map(Setting::getId).collect(Collectors.toList());

    // All default ids should be returned in response
    Assertions.assertTrue(ids.stream().map(defaultIds::contains).reduce((a, b) -> a && b).get());

  }

  @Test
  void getAllAsNormie() {
    given()
        .header(authHeaderAdmin)
        .when()
        .get(SETTINGS_URL)
        .then()
        .statusCode(200)
        .body("$", hasKey("data"))
        .body("data.size()", is(DefaultSettings.all.size()));
  }

}
