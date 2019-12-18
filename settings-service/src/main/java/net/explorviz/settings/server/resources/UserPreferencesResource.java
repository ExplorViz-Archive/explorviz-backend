package net.explorviz.settings.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.services.AuthorizationService;
import net.explorviz.settings.services.UserPreferenceRepository;
import net.explorviz.settings.services.UserPreferenceService;
import net.explorviz.settings.services.validation.PreferenceValidationException;
import net.explorviz.shared.querying.Query;
import net.explorviz.shared.querying.QueryResult;
import net.explorviz.shared.security.filters.Secure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource to access {@link UserPreference}, i.e. to handle user specific settings.
 *
 */
@Path("v1/preferences")
@Tag(name = "Preferences")
@SecurityRequirement(name = "token")
@Secure
@PermitAll
public class UserPreferencesResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserPreferencesResource.class);
  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private static final String NO_ADMIN_MESSAGE =
      "Blocked attempt to access unowned settings without being admin";

  private final UserPreferenceRepository userPrefRepo;
  private final UserPreferenceService userPrefService;

  private final AuthorizationService authService;



  /**
   * Creates the endpoint.
   *
   * @param userPrefService service for handling user preferences
   * @param customSettingRepo repository for user preferences
   */
  @Inject
  public UserPreferencesResource(final UserPreferenceRepository customSettingRepo,
      final UserPreferenceService userPrefService,
      final AuthorizationService authorizationService) {
    super();
    this.authService = authorizationService;
    this.userPrefRepo = customSettingRepo;
    this.userPrefService = userPrefService;
  }

  /**
   * Access all preferences, possible filtered for a specific of a user.
   *
   * @return a list of all user preferences for a given user.
   */
  @GET
  @Produces(MEDIA_TYPE)
  @PermitAll
  @Operation(summary = "Get a user's preferences")
  @ApiResponse(responseCode = "200",
      description = "Return all preferences of the user with the given id.", content = @Content(
          array = @ArraySchema(schema = @Schema(implementation = UserPreference.class))))
  @ApiResponse(responseCode = "403", description = "A user can only access its own preferences."
      + " Admins can access any users preferences.")
  @Parameters({
      @Parameter(in = ParameterIn.QUERY, name = "page[size]",
          description = "Controls the size, i.e., amount of entities, of each page."),
      @Parameter(in = ParameterIn.QUERY, name = "page[number]",
          description = "Index of the page to return."),
      @Parameter(in = ParameterIn.QUERY, name = "filter[user]",
          description = "User id to filter preferences for")})
  public QueryResult<UserPreference> getPreferencesForUser(@Context final HttpHeaders headers,
      @Context final UriInfo uriInfo) {

    final Query<UserPreference> query = Query.fromParameterMap(uriInfo.getQueryParameters(true));

    final String uid =
        query.getFilters().get("user") == null ? "" : query.getFilters().get("user").get(0);

    final String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);

    if (!this.canAccess(authHeader, uid)) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info(NO_ADMIN_MESSAGE);
      }
      throw new ForbiddenException();
    }

    return this.userPrefRepo.query(query);
  }



  /**
   * Deletes an UserPereference such that the default value applies again for the given user.
   *
   * @param prefId the id of the preference to delete
   * @return 204 (not content) on success
   */
  @DELETE
  @PermitAll
  @Path("{id}")
  @Operation(summary = "Delete a preference",
      description = "If a preference is delete, the default value of the corresponding "
          + "setting applies again.")
  @ApiResponse(responseCode = "403", description = "A user can only access its own preferences. "
      + "Admins can access any users preferences.")
  @ApiResponse(responseCode = "204", description = "The preference was deleted")
  public Response resetToDefault(
      @Parameter(description = "Id of the preference",
          required = true) @PathParam("id") final String prefId,
      @Context final HttpHeaders headers) {

    // Find the preference object
    final UserPreference found = this.userPrefRepo.find(prefId).orElseThrow(NotFoundException::new);

    if (!this.canAccess(headers.getHeaderString(HttpHeaders.AUTHORIZATION), found.getUserId())) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info(NO_ADMIN_MESSAGE);
      }
      throw new ForbiddenException();
    }

    this.userPrefRepo.delete(prefId);

    return Response.noContent().build();
  }

  /**
   * Updates the value of an existing setting.
   *
   * @param prefId the new preference content
   * @param headers the http headers (provided by jersey)
   * @param updatedPref the updated preference object
   * @return the {@link UserPreference} object after it has been updated
   */
  @PATCH
  @Produces(MEDIA_TYPE)
  @Path("{id}")
  @PermitAll
  @Operation(summary = "Update a preference value")
  @ApiResponse(responseCode = "200",
      description = "Value was updated, the repsonse contains the update preference.",
      content = @Content(schema = @Schema(implementation = UserPreference.class)))
  @ApiResponse(responseCode = "400", description = "Update request is invalid.")
  @ApiResponse(responseCode = "403", description = "A user can only access its own preferences. "
      + "Admins can access any users preferences.")
  @RequestBody(content = @Content(schema = @Schema(implementation = UserPreference.class)),
      description = "The updated preference. Only the value can be updated. "
          + "The id of the preference object, the id of the user and, "
          + "the id of the corresponding setting must stay the same.")
  public UserPreference updatePref(
      @Parameter(
          description = "Id of the preference to update.") @PathParam("id") final String prefId,
      @Context final HttpHeaders headers, final UserPreference updatedPref) {

    final UserPreference found = this.userPrefRepo.find(prefId).orElseThrow(NotFoundException::new);

    // Check if authorized to alter
    if (!this.canAccess(headers.getHeaderString(HttpHeaders.AUTHORIZATION), found.getUserId())) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info(NO_ADMIN_MESSAGE);
      }
      throw new ForbiddenException();
    }

    // ONLY values can change
    if (!found.getId().equals(updatedPref.getId())) {
      throw new BadRequestException(String
          .format("Cannot update ids (orginal: %s, new: %s)", found.getId(), updatedPref.getId()));
    }

    if (!found.getSettingId().equals(updatedPref.getSettingId())
        || !found.getUserId().equals(updatedPref.getUserId())) {
      throw new BadRequestException("Can only update values");
    }

    // Validate new value
    try {
      this.userPrefService.validate(updatedPref);
    } catch (final PreferenceValidationException e) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Invalid preference: %s", e.getMessage());
      }
      throw new BadRequestException(e.getMessage());
    }



    found.setValue(updatedPref.getValue());

    this.userPrefRepo.createOrUpdate(found);
    return found;
  }

  /**
   * Creates a new custom setting.
   *
   * @param preference the custom setting
   * @return HTTP 200 (OK) iff the custom setting was saved
   */
  @POST
  @Consumes(MEDIA_TYPE)
  @Operation(summary = "Create a preference value")
  @ApiResponse(responseCode = "200",
      description = "Value was updated, the repsonse contains the update preference.",
      content = @Content(schema = @Schema(implementation = UserPreference.class)))
  @ApiResponse(responseCode = "400",
      description = "Can't create the preference. This is either the case if"
          + " the constraints of the corresponding setting are not satisfied"
          + " or a preference with the same user and setting already exists.")
  @ApiResponse(responseCode = "403", description = "A user can only access its own preferences. "
      + "Admins can access any users preferences.")
  @RequestBody(content = @Content(schema = @Schema(implementation = UserPreference.class)),
      description = "The preference to create. The id field must not be set. "
          + "Only a single preference can exist per user and setting.")
  public UserPreference createPreference(final UserPreference preference,
      @Context final HttpHeaders headers) {

    // User can only create preferences for themselves, admin can for all users
    if (!this.canAccess(headers.getHeaderString(HttpHeaders.AUTHORIZATION),
        preference.getUserId())) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Blocked attempt to create preference for other user");
      }
      throw new ForbiddenException();
    }

    // Validate
    try {
      this.userPrefService.validate(preference);
    } catch (final PreferenceValidationException e) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Invalid preference: %s", e.getMessage());
      }
      throw new BadRequestException(e.getMessage());
    }
    try {
      return this.userPrefRepo.createOrUpdate(preference);
    } catch (final IllegalStateException e) {
      throw new BadRequestException(e.getMessage());
    }

  }

  /**
   * Auxiliary method with checks if the auth header is from an admin or if the user id in the
   * header matches the given user id.
   *
   * @param authHeader the (full) authorization header (i.e. bearer token)
   * @param uid a user id
   * @return {@code true} if and only if the token belongs to an admin or the given user ids match.
   */
  private boolean canAccess(final String authHeader, final String uid) {
    return this.authService.isSameUser(uid, authHeader) || this.authService.isAdmin(authHeader);
  }



}
