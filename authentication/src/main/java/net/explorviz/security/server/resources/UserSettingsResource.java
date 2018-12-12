package net.explorviz.security.server.resources;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import net.explorviz.shared.security.model.UserSettings;

@Path("v1/settings")
public class UserSettingsResource {

  // private static final Logger LOGGER = LoggerFactory.getLogger(RoleResource.class); // NOPMD

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String ADMIN_ROLE = "admin";
  private static final long HARD_CODED_SETTINGS_ID = 1L;

  @GET
  @Path("{id}")
  @RolesAllowed({ADMIN_ROLE})
  @Produces(MEDIA_TYPE)
  public UserSettings getDefaultSettings(@PathParam("id") final long id) {
    if (id == HARD_CODED_SETTINGS_ID) {
      return new UserSettings();
    } else {
      throw new BadRequestException("No settings object with this id.");
    }

  }

}
