package net.explorviz.security.server.resources;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ws.rs.NotFoundException;
import net.explorviz.shared.security.model.settings.SettingDescriptor;
import org.junit.jupiter.api.Test;

public class SettingDescriptorResourceTest {



  @Test
  public void testSettingsInfo() {
    final SettingsDescriptorResource settingDescriptorResource =
        new SettingsDescriptorResource("showFpsCounter");
    final SettingDescriptor<Boolean> info =
        (SettingDescriptor<Boolean>) settingDescriptorResource.settingDescriptor();
    assertFalse("Unmatching descriptor", info.getDefaultValue());
  }

  @Test
  public void testUnknownSettingInfo() {
    assertThrows(NotFoundException.class,
        () -> new SettingsDescriptorResource("UnknownSetting").settingDescriptor());
  }

}
