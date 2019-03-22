package net.explorviz.security.server.resources;

import static org.junit.Assert.assertEquals;

import com.github.jasminb.jsonapi.ResourceConverter;
import java.util.Arrays;
import javax.inject.Inject;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import net.explorviz.security.server.main.DependencyInjectionBinder;
import net.explorviz.security.services.TokenService;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import net.explorviz.shared.security.model.settings.UserSettings;
import org.eclipse.jetty.http.HttpHeader;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

/**
 * Endpoint test for {@link UserSettingsResource} as well as {@link SettingsDescriptorResource}.
 */
public class SettingsEndpointTest extends JerseyTest {
  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String BASE_URL = "v1/settings/";

  @Inject
  private ResourceConverter jsonApiConverter;

  @Inject
  private TokenService tokenService;


  private String adminToken;

  @Override
  public void setUp() throws Exception {
    final DependencyInjectionBinder binder = new DependencyInjectionBinder();
    final ServiceLocator locator = ServiceLocatorUtilities.bind(binder);
    locator.inject(this);

    final User admin = new User("Admin");
    admin.setRoles(Arrays.asList(new Role("admin")));
    final User normie = new User("Normie");

    this.adminToken = "Bearer " + this.tokenService.issueNewToken(admin);

    super.setUp();
  }



  @Override
  protected void configureClient(final ClientConfig config) {
    // Otherwise we can't send PATCH-Requests
    config.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
  }


  @Override
  protected Application configure() {

    final ResourceConfig c =
        new ResourceConfig(new net.explorviz.security.server.main.Application());
    return c;
  }

  @Test
  public void testDefaultSettings() {
    final Response response = this.target(BASE_URL).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken).get();

    final UserSettings retrieved = this.jsonApiConverter
        .readDocument(response.readEntity(byte[].class), UserSettings.class).get();

    assertEquals("Did not return default user settings", new UserSettings(), retrieved);

  }
}
