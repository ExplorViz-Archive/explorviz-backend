package net.explorviz.security.server.resources;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import net.explorviz.shared.security.model.settings.DefaultSettings;
import net.explorviz.shared.security.model.settings.SettingDescriptor;

public class SettingsDescriptorResource {


  private static final String MEDIA_TYPE = "application/vnd.api+json";



  @GET
  @Path("{id}")
  @PermitAll
  @Produces(MEDIA_TYPE)
  public SettingDescriptor settingDescriptor(@PathParam("id") final String id) {
    if (DefaultSettings.booleanSettings().containsKey(id)) {
      return DefaultSettings.booleanSettings().get(id);
    }

    if (DefaultSettings.numericSettings().containsKey(id)) {
      return DefaultSettings.numericSettings().get(id);
    }

    if (DefaultSettings.stringSettings().containsKey(id)) {
      return DefaultSettings.stringSettings().get(id);
    }

    throw new NotFoundException(String.format("Setting with id %s does not exist", id));
  }

}
