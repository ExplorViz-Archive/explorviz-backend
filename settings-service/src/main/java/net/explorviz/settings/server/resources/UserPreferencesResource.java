package net.explorviz.settings.server.resources;

import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.services.AuthorizationService;
import net.explorviz.settings.services.UserPreferenceRepository;
import net.explorviz.settings.services.UserPreferenceService;
import net.explorviz.settings.services.validation.PreferenceValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource to access {@link UserPreference}, i.e. to handle user specific settings.
 *
 */
@Path("v1/settings/custom")
public class UserPreferencesResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserPreferencesResource.class);
  private static final String MEDIA_TYPE = "application/vnd.api+json";


  private final UserPreferenceRepository customSettingsRepo;
  private final UserPreferenceService customSettingService;

  private final AuthorizationService authService;



  /**
   * Creates the endpoint.
   *
   * @param customSettingService service for handling user preferences
   * @param customSettingRepo repository for user preferences
   */
  @Inject
  public UserPreferencesResource(final UserPreferenceRepository customSettingRepo,
      final UserPreferenceService customSettingService,
      final AuthorizationService authorizationService) {
    super();
    this.authService = authorizationService;
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
  @RolesAllowed("ADMIN")
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
  @PermitAll
  public List<UserPreference> getForUser(@PathParam("userId") final String uid,
      @Context final HttpHeaders headers) {


    if (!this.authService.isSameUser(uid, headers.getHeaderString(HttpHeaders.AUTHORIZATION))) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info(String.format(
            "Blocked attempt of user with id %s trying to access settings of other user", uid));
      }
      throw new ForbiddenException();
    }

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
  @PermitAll
  public UserPreference createPreference(final UserPreference customSetting,
      @Context final HttpHeaders headers) {

    // Users can only update preferences for themselves
    if (!this.authService.isSameUser(customSetting.getUserId(),
        headers.getHeaderString(HttpHeaders.AUTHORIZATION))) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Blocked attempt of user trying to create preference for other user");
      }
      throw new ForbiddenException();
    }

    try {
      this.customSettingService.validate(customSetting);
    } catch (final PreferenceValidationException e) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Invalid preference: %s", e.getMessage());
      }
      throw new BadRequestException(e.getMessage());
    }
    return this.customSettingsRepo.create(customSetting);
  }


}
