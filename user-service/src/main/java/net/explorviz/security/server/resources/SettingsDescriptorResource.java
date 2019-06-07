package net.explorviz.security.server.resources;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;
import net.explorviz.shared.security.model.settings.DefaultSettings;
import net.explorviz.shared.security.model.settings.SettingDescriptor;

public class SettingsDescriptorResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private final String id;



  public SettingsDescriptorResource(final String id) {
    this.id = id;
  }

  @GET
  @PermitAll
  @Produces(MEDIA_TYPE)
  public SettingDescriptor<?> settingDescriptor() {
    if (DefaultSettings.booleanSettings().containsKey(this.id)) {
      return DefaultSettings.booleanSettings().get(this.id);
    }

    if (DefaultSettings.numericSettings().containsKey(this.id)) {
      return DefaultSettings.numericSettings().get(this.id);
    }

    if (DefaultSettings.stringSettings().containsKey(this.id)) {
      return DefaultSettings.stringSettings().get(this.id);
    }

    throw new NotFoundException(String.format("Setting with id %s does not exist", this.id));
  }

}
