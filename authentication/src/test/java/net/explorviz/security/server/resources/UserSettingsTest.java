package net.explorviz.security.server.resources;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.NotFoundException;
import net.explorviz.security.server.main.DependencyInjectionBinder;
import net.explorviz.security.services.UserMongoCrudService;
import net.explorviz.security.testutils.TestDatasourceFactory;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.settings.SettingDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import xyz.morphia.Datastore;

/**
 * Test for settings resource.
 *
 */
public class UserSettingsTest {

  @Inject
  private UserMongoCrudService userCrudService;

  @Inject
  private Datastore datastore;


  private UserSettingsResource settingsResource;


  @Before
  public void setUp() {
    final AbstractBinder b = new DependencyInjectionBinder();
    b.bindFactory(TestDatasourceFactory.class).to(Datastore.class).in(Singleton.class).ranked(2);
    final ServiceLocator locator = ServiceLocatorUtilities.bind(b);
    locator.inject(this);

    this.settingsResource = new UserSettingsResource();

  }

  @After
  public void tearDown() {
    this.datastore.getCollection(User.class).drop();
  }

  @Test
  public void testUpdateSettings() {

    final User u = new User("testuser");
    u.setPassword("testPassword");


    u.getSettings().put("appVizTransparencyIntensity", 0.5);
    this.userCrudService.saveNewEntity(u);

    final User u1 = this.userCrudService.getEntityById(u.getId()).orElse(null);
    assertEquals(0.5, u1.getSettings().getNumericAttribute("appVizTransparencyIntensity"));
  }


  @Test(expected = IllegalArgumentException.class)
  public void testUnknownSetting() {

    final User u = new User("testuser");
    u.setPassword("testPassword");


    u.getSettings().put("unknownsetting", 0.5);
    this.userCrudService.saveNewEntity(u);
  }


  @Test
  public void testSettingsInfo() {
    final SettingDescriptor<Number> info = this.settingsResource.settingsInfo("showFpsCounter");
    assertEquals(false, info.getDefaultValue());
  }

  @Test(expected = NotFoundException.class)
  public void testUnknownSettingInfo() {
    final SettingDescriptor<Number> info = this.settingsResource.settingsInfo("unknown");
  }

}
