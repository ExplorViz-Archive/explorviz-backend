package net.explorviz.security.server.resources;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ws.rs.NotFoundException;
import net.explorviz.shared.security.model.settings.SettingDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SettingDescriptorResourceTest {

  private SettingsDescriptorResource settingDescriptorResource;


  @BeforeEach
  public void setUp() {
    this.settingDescriptorResource = new SettingsDescriptorResource();
  }

  @Test
  public void testSettingsInfo() {
    final SettingDescriptor<Boolean> info =
        this.settingDescriptorResource.settingDescriptor("showFpsCounter");
    assertFalse("Unmatching descriptor", info.getDefaultValue());
  }

  @Test
  public void testUnknownSettingInfo() {
    assertThrows(NotFoundException.class,
        () -> this.settingDescriptorResource.settingDescriptor("unknown"));
  }

}
