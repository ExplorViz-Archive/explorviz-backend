package net.explorviz.settings.server.resources.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import io.restassured.http.Header;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.server.resources.test.helper.AuthorizationHelper;
import net.explorviz.settings.server.resources.test.helper.DefaultSettings;
import net.explorviz.settings.server.resources.test.helper.JsonAPIListMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SettingsInfoRetrieval {

  private static final String SETTINGS_URL = "http://localhost:8090/v1/settings";

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


  @Test
  @SuppressWarnings("unchecked")
  void getAllAsAdmin() {
    final List<Setting> retrieved = given().header(this.authHeaderNormie)
        .when()
        .get(SETTINGS_URL)
        .then()
        .statusCode(200)
        .body("$", hasKey("data"))
        .body("data.size()", is(DefaultSettings.all.size()))
        .extract()
        .body()
        .as(List.class, new JsonAPIListMapper<>(Setting.class));
    final List<String> ids = retrieved.stream().map(Setting::getId).collect(Collectors.toList());
    final List<String> defaultIds =
        DefaultSettings.all.stream().map(Setting::getId).collect(Collectors.toList());

    // All default ids should be returned in response
    Assertions.assertTrue(ids.stream().map(defaultIds::contains).reduce((a, b) -> a && b).get());

  }

  @Test
  void getAllAsNormie() {
    given().header(this.authHeaderAdmin)
        .when()
        .get(SETTINGS_URL)
        .then()
        .statusCode(200)
        .body("$", hasKey("data"))
        .body("data.size()", is(DefaultSettings.all.size()));
  }

  @Test
  void pagniationFirstPage() {
    final int size = 2;
    final int num = 0;
    given().contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .params("page[size]", size, "page[number]", num)
        .when()
        .get(SETTINGS_URL)
        .then()
        .statusCode(200)
        .body("data.size()", Matchers.is(size))
        .body("links", hasKey("first"))
        .body("links", hasKey("next"))
        .body("links", hasKey("last"))
        .body("links", not(hasKey("prev")));
  }

  @Test
  void paginationMiddlePage() {
    final int size = 1;
    final int num = DefaultSettings.all.size() / 2;
    given().contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .params("page[size]", size, "page[number]", num)
        .when()
        .get(SETTINGS_URL)
        .then()
        .statusCode(200)
        .body("data.size()", Matchers.is(size))
        .body("links", hasKey("first"))
        .body("links", hasKey("prev"))
        .body("links", hasKey("last"))
        .body("links", hasKey("next"));
  }


  @Test
  void filterByOrigin() {
    final String origin = DefaultSettings.origin;
    given().contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .params("filter[origin]", origin)
        .when()
        .get(SETTINGS_URL)
        .then()
        .statusCode(200)
        .body("data.size()", is(DefaultSettings.all.size()));
  }

  @Test
  @SuppressWarnings("unchecked")
  void filterByType() {
    final int rangeSettings = (int) DefaultSettings.all.stream()
        .filter(s -> s.getClass().equals(RangeSetting.class))
        .count();
    final List<Setting> retrieved = given().contentType(MEDIA_TYPE)
        .header(this.authHeaderAdmin)
        .params("filter[type]", "rangesetting")
        .when()
        .get(SETTINGS_URL)
        .then()
        .statusCode(200)
        .body("data.size()", is(rangeSettings))
        .extract()
        .body()
        .as(List.class, new JsonAPIListMapper<>(Setting.class));

    final int retrievedRangeSettings =
        (int) retrieved.stream().filter(s -> s.getClass().equals(RangeSetting.class)).count();
    Assert.assertEquals(retrieved.size(), retrievedRangeSettings);


  }

}
