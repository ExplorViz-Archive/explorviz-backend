package net.explorviz.security.server.resources;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserMongoCrudService;
import net.explorviz.shared.security.model.TokenDetails;
import net.explorviz.shared.security.model.User;
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


  /**
   * Returns the settings of a user with a given id.
   *
   * @param id the user id
   * @return the {@link UserSettings}
   */
  // @GET
  @Path("{id}")
  @PermitAll
  @Produces(MEDIA_TYPE)
  public UserSettings getSettingsForUser(@PathParam("id") final long id,
      @Context final HttpHeaders headers) {


    final User u = this.userService.getEntityById(id)
        .orElseThrow(() -> new NotFoundException("User does not exist"));


    final TokenDetails details = this.tokenService
        .parseToken(headers.getHeaderString(HttpHeaders.AUTHORIZATION).substring(7));


    if (details.getUserId() != id && !details.getRoles().contains(ADMIN_ROLE)) {
      throw new ForbiddenException();
    }

    final UserSettings settings = u.getSettings();

    final boolean changed = DefaultSettings.addMissingDefaults(settings);

    if (changed) {
      // Update user with newer settings
      this.userService.updateEntity(u);
    }

    return settings;

  }


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
