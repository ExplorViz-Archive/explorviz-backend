package net.explorviz.security.server.resources;

import static org.junit.Assert.assertEquals;

import com.github.jasminb.jsonapi.ResourceConverter;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Application;
import net.explorviz.security.server.main.DependencyInjectionBinder;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserMongoCrudService;
import net.explorviz.security.testutils.TestDatasourceFactory;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import net.explorviz.shared.security.model.settings.UserSettings;
import org.eclipse.jetty.http.HttpHeader;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import xyz.morphia.Datastore;

/**
 * Test for settings resource endpoint.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class UserSettingsResourceEndpointTest extends JerseyTest {

  private static final String URL_PATH = "v1/settings/";
  private static final String AUTH_METHOD = "Bearer";

  @Inject
  private ResourceConverter resourceConverter;

  @Inject
  private TokenService tokenService;

  @Inject
  private UserMongoCrudService userService;

  @Inject
  private Datastore datastore;

  private final UserSettings userSettings = new UserSettings();

  private String adminToken;
  private String normieToken;



  /*
   * Setup
   */

  @Override
  public void setUp() throws Exception {

    final DependencyInjectionBinder binder = new DependencyInjectionBinder();
    binder.bindFactory(TestDatasourceFactory.class).to(Datastore.class).in(Singleton.class)
        .ranked(2);
    // Inject dependencies
    final ServiceLocator locator = ServiceLocatorUtilities.bind(binder);

    locator.inject(this);

    // Create tokens for random users
    final User admin = new User("Admin");
    admin.setRoles(Arrays.asList(new Role(1L, "admin")));
    final User normie = new User("Normie");

    this.adminToken = AUTH_METHOD + " " + this.tokenService.issueNewToken(admin);
    this.normieToken = AUTH_METHOD + " " + this.tokenService.issueNewToken(normie);

    super.setUp();
  }

  @Override
  public void tearDown() throws Exception {
    this.datastore.getCollection(User.class).drop();
    super.tearDown();
  }


  @Override
  protected Application configure() {
    final ResourceConfig c =
        new ResourceConfig(new net.explorviz.security.server.main.Application());
    c.register(new AbstractBinder() {
      @Override
      protected void configure() {
        this.bind(UserMongoCrudService.class).to(UserMongoCrudService.class).in(Singleton.class)
            .ranked(10);
        this.bindFactory(TestDatasourceFactory.class).to(Datastore.class).in(Singleton.class)
            .ranked(2);
      }
    });
    return c;
  }

  /*
   * Tests
   */

  @Test
  public void checkDefaultSettings() {

    final byte[] rawResponseBody = this.target(URL_PATH).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken).get(byte[].class);

    final UserSettings obtainedUserSettings =
        this.resourceConverter.readDocument(rawResponseBody, UserSettings.class).get();

    assertEquals("Settings do not match", this.userSettings, obtainedUserSettings);
  }

  @Test(expected = ForbiddenException.class)
  public void checkDefaultSettingsAsNormie() {

    this.target(URL_PATH).request().header(HttpHeader.AUTHORIZATION.asString(), this.normieToken)
        .get(String.class);
  }

  @Test
  @Ignore // User always knows its own settings
  public void getOwnSettings() {

    final User user = new User("someuser");
    user.setPassword("abc");

    this.userService.saveNewEntity(user);

    final String token = AUTH_METHOD + " " + this.tokenService.issueNewToken(user);

    System.out.println(this.tokenService.parseToken(token.substring(7)).getUserId());

    final String path = URL_PATH + user.getId();


    final byte[] rawResponseBody = this.target(path).request()
        .header(HttpHeader.AUTHORIZATION.asString(), token).get(byte[].class);

    final UserSettings obtainedUserSettings =
        this.resourceConverter.readDocument(rawResponseBody, UserSettings.class).get();

    assertEquals("Settings do not match", this.userSettings, obtainedUserSettings);
  }

}
