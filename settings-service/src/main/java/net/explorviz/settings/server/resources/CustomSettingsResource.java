package net.explorviz.settings.server.resources;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.services.UserPreferenceRepository;
import net.explorviz.settings.services.UserPreferenceService;
import net.explorviz.settings.services.SettingValidationException;

/**
 * Resource to access {@link UserPreference}, i.e. to handle user specific settings.
 *
 */
@Path("v1/settings/custom")
public class CustomSettingsResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";


  private final UserPreferenceRepository customSettingsRepo;
  private final UserPreferenceService customSettingService;


  /**
   * Creates the endpoint.
   *
   * @param customSettingService service for handling user preferences
   * @param customSettingRepo repository for user preferences
   */
  @Inject
  public CustomSettingsResource(final UserPreferenceRepository customSettingRepo,
      final UserPreferenceService customSettingService) {
    super();
    this.customSettingsRepo = customSettingRepo;
    this.customSettingService = customSettingService;
  }

  /**
   * Access all user settings.
   *
   * @return a list of all user settings
   */
  @GET
  @Produces(MEDIA_TYPE)
  public List<UserPreference> getAll() {
    return this.customSettingsRepo.findAll();
  }

  /**
   * Access the custom settings of a specific user.
   *
   * @param uid the id of the user
   * @return a list of {@link UserPreference} describing the users preferences.
   */
  @GET
  @Produces(MEDIA_TYPE)
  @Path("/{userId}")
  public List<UserPreference> getForUser(@PathParam("userId") final String uid) {
    return this.customSettingService.getCustomsForUser(uid);
  }

  /**
   * Creates a new custom setting.
   *
   * @param customSetting the custom setting
   * @return HTTP 200 (OK) iff the custom setting was saved
   */
  @POST
  @Consumes(MEDIA_TYPE)
  public Response createCustomSetting(final UserPreference customSetting) {
    try {
      this.customSettingService.validate(customSetting);
      this.customSettingsRepo.create(customSetting);
      return Response.ok().build();
    } catch (final SettingValidationException e) {
      // TODO Auto-generated catch block
      throw new BadRequestException(e.getMessage());
    }
  }

}
