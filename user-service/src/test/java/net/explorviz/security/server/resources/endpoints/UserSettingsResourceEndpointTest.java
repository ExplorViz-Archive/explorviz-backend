package net.explorviz.security.server.resources.endpoints;

import static org.junit.Assert.assertEquals;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.ForbiddenException;
import net.explorviz.security.server.main.DependencyInjectionBinder;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserCrudException;
import net.explorviz.security.services.UserService;
import net.explorviz.security.testutils.TestDatasourceFactory;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.settings.UserSettings;
import org.eclipse.jetty.http.HttpHeader;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
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
public class UserSettingsResourceEndpointTest extends EndpointTest {

  private static final String URL_PATH = "v1/settings/";
  private static final String AUTH_METHOD = "Bearer";


  @Inject
  private TokenService tokenService;

  @Inject
  private UserService userService;

  @Inject
  private Datastore datastore;

  private final UserSettings userSettings = new UserSettings();



  @Override
  protected void overrideTestBindings(final DependencyInjectionBinder binder) {
    binder.bindFactory(TestDatasourceFactory.class)
        .to(Datastore.class)
        .in(Singleton.class)
        .ranked(2);
  }


  @Override
  public void tearDown() throws Exception {
    this.datastore.getCollection(User.class).drop();
    super.tearDown();
  }



  @Override
  protected AbstractBinder overrideApplicationBindings() {
    return new DependencyInjectionBinder() {
      @Override
      public void configure() {
        super.configure();
        this.bind(UserService.class).to(UserService.class).in(Singleton.class).ranked(10);
        this.bindFactory(TestDatasourceFactory.class)
            .to(Datastore.class)
            .in(Singleton.class)
            .ranked(2);
      }
    };
  }


  @Test
  public void checkDefaultSettings() {

    final byte[] rawResponseBody = this.target(URL_PATH)
        .request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getAdminToken())
        .get(byte[].class);

    final UserSettings obtainedUserSettings =
        this.getJsonApiConverter().readDocument(rawResponseBody, UserSettings.class).get();

    assertEquals("Settings do not match", this.userSettings, obtainedUserSettings);
  }

  @Test(expected = ForbiddenException.class)
  public void checkDefaultSettingsAsNormie() {

    this.target(URL_PATH)
        .request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getNormieToken())
        .get(String.class);
  }



  @Test
  @Ignore // User always knows its own settings
  public void testOwnSettings() throws UserCrudException {

    final User user = new User("someuser");
    user.setPassword("abc");

    this.userService.saveNewEntity(user);

    final String token = AUTH_METHOD + " " + this.tokenService.issueNewToken(user);

    final String path = URL_PATH + user.getId();


    final byte[] rawResponseBody = this.target(path)
        .request()
        .header(HttpHeader.AUTHORIZATION.asString(), token)
        .get(byte[].class);

    final UserSettings obtainedUserSettings =
        this.getJsonApiConverter().readDocument(rawResponseBody, UserSettings.class).get();

    assertEquals("Settings do not match", this.userSettings, obtainedUserSettings);
  }

}
