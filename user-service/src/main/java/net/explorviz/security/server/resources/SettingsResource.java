package net.explorviz.security.server.resources;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import net.explorviz.shared.security.model.settings.UserSettings;

/**
 * Handle user settings.
 *
 */
@Path("v1/settings")
public class SettingsResource {

  // private static final Logger LOGGER = LoggerFactory.getLogger(RoleResource.class); // NOPMD

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String ADMIN_ROLE = "admin";



  /**
   * Finds the descriptor of the setting with the given id.
   *
   * @param id id of the setting
   * @return the descriptor
   */
  @Path("{id}/info")
  public SettingsDescriptorResource settingsInfo(@PathParam("id") final String id) {
    return new SettingsDescriptorResource(id);

  }


  /**
   * Returns the default settings.
   */
  @GET
  @RolesAllowed({ADMIN_ROLE})
  @Produces(MEDIA_TYPE)
  public UserSettings getDefaultSettings() {
    return new UserSettings();
  }



}
