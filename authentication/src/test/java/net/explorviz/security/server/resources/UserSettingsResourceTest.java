package net.explorviz.security.server.resources;

import static org.junit.Assert.assertEquals;

import net.explorviz.shared.security.model.UserSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test for settings resource.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class UserSettingsResourceTest {

  @InjectMocks
  private UserSettingsResource settingsResource;

  @Test
  public void testGetDefaultSettings() {
    final UserSettings userSettings = new UserSettings();

    final UserSettings defaultUserSettings = this.settingsResource.getDefaultSettings(1);

    assertEquals("Settings do not match", userSettings, defaultUserSettings);
  }

}
