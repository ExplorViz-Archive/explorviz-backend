package net.explorviz.security.server.resources;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserMongoCrudService;
import net.explorviz.shared.security.model.settings.DefaultSettings;
import net.explorviz.shared.security.model.settings.SettingDescriptor;
import net.explorviz.shared.security.model.settings.UserSettings;

@Path("v1/settings")
public class UserSettingsResource {

  // private static final Logger LOGGER = LoggerFactory.getLogger(RoleResource.class); // NOPMD

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String ADMIN_ROLE = "admin";


  @Inject
  private UserMongoCrudService userService;

  @Inject
  TokenService tokenService;



  @GET
  @Path("{id}")
  @PermitAll
  @Produces(MEDIA_TYPE)
  public SettingDescriptor settingsInfo(@PathParam("id") final String id) {

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


  /**
   * Returns the default settings
   */
  @GET
  @RolesAllowed({ADMIN_ROLE})
  @Produces(MEDIA_TYPE)
  public UserSettings getDefaultSettings() {
    return new UserSettings();
  }



}
