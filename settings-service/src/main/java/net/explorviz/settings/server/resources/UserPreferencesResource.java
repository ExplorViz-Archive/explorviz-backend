package net.explorviz.settings.server.resources;

import java.util.Arrays;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource to access {@link UserPreference}, i.e. to handle user specific settings.
 *
 */
@Path("v1/users")
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
   * Access preferences of a user.
   *
   * @param uid query parameter that denotes the user.
   * @return a list of all user preferences for a given user.
   */
  @GET
  @Produces(MEDIA_TYPE)
  @PermitAll
  @Path("{uid}/settings/preferences")
  public QueryResult<UserPreference> getPreferencesForUser(@Context final HttpHeaders headers,
      @Context final UriInfo uriInfo, @PathParam("uid") final String uid) {

    final Query<UserPreference> query = Query.fromParameterMap(uriInfo.getQueryParameters(true));

    // Workaround to query only for a specific user
    // Technically the path parameter 'uid' is handled as a query parameter
    query.getFilter().put("userId", Arrays.asList(uid));

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
   * @param 204 (not content) on success
   */
  @DELETE
  @PermitAll
  @Path("settings/preferences/{id}")
  public Response resetToDefault(@PathParam("id") final String prefId,
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
  @Path("settings/preferences/{prefId}")
  @PermitAll
  public UserPreference updatePref(@PathParam("prefId") final String prefId,
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
      throw new BadRequestException(String.format("Cannot update ids (orginal: %s, new: %s)",
          found.getId(), updatedPref.getId()));
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
  @PermitAll
  @Path("settings/preferences")
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
