package net.explorviz.security.server.resources.endpoints;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import net.explorviz.security.server.resources.SettingsDescriptorResource;
import net.explorviz.security.server.resources.SettingsResource;
import net.explorviz.shared.security.model.settings.BooleanSettingDescriptor;
import net.explorviz.shared.security.model.settings.UserSettings;
import org.eclipse.jetty.http.HttpHeader;
import org.junit.Test;

/**
 * Endpoint test for {@link SettingsResource} as well as {@link SettingsDescriptorResource}.
 */
public class SettingsEndpointTest extends EndpointTest {
  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String BASE_URL = "v1/settings/";



  @Test
  public void testDefaultSettings() {
    final Response response = this.target(BASE_URL).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getAdminToken()).get();

    final UserSettings retrieved = this.getJsonApiConverter()
        .readDocument(response.readEntity(byte[].class), UserSettings.class).get();

    assertEquals("Did not return default user settings", new UserSettings(), retrieved);
  }

  @Test
  public void testSettingDescriptor() {
    final String settingId = "showFpsCounter";
    final Response response = this.target(BASE_URL + settingId + "/info").request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getAdminToken()).get();

    this.getJsonApiConverter().registerType(BooleanSettingDescriptor.class);


    // Requires default constructors in shared
    /*
     * final SettingDescriptor retrieved = this.jsonApiConverter
     * .readDocument(response.readEntity(byte[].class), SettingDescriptor.class).get();
     */

    assertEquals("Did not retrieve setting descriptor", 200, response.getStatus());
  }



}
