package net.explorviz.security.server.resources;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import net.explorviz.security.services.RoleService;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserMongoCrudService;
import net.explorviz.security.util.PasswordStorage;
import net.explorviz.security.util.PasswordStorage.CannotPerformOperationException;
import net.explorviz.shared.security.model.TokenDetails;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import net.explorviz.shared.security.model.settings.DefaultSettings;
import net.explorviz.shared.security.model.settings.UserSettings;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides endpoints for user management.
 *
 */
@Path("v1/users")
public class UserResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

  private static final String MSG_INVALID_PASSWORD = "Invalid password";
  private static final String MSG_INVALID_USERNAME = "Invalid username";
  private static final String MSG_USER_NOT_RETRIEVED = "Could not retrieve user ";
  private static final String MSG_USER_NOT_UPDATED = "Could not update user ";


  private static final String ADMIN_ROLE = "admin";

  @Inject
  private UserMongoCrudService userCrudService;

  @Inject
  private RoleService roleService;

  @Inject
  private TokenService tokenService;

  // CHECKSTYLE.OFF: Cyclomatic

  /**
   * Creates and persists a new user.
   *
   * @param user the user to create
   * @return the user object, that was saved
   */
  @POST
  @Consumes(MEDIA_TYPE)
  @Produces(MEDIA_TYPE)
  @RolesAllowed({ADMIN_ROLE})
  public User newUser(final User user) { // NOPMD

    if (user.getUsername() == null || user.getUsername().equals("")) {
      throw new BadRequestException(MSG_INVALID_USERNAME);
    }

    if (user.getPassword() == null || user.getPassword().equals("")) {
      throw new BadRequestException(MSG_INVALID_PASSWORD);
    }

    if (user.getId() != null) {
      throw new BadRequestException("Can't create user with id. Payload must not have an id.");
    }

    for (final Role r : user.getRoles()) {
      if (!this.roleService.getAllRoles().contains(r)) {
        throw new BadRequestException("Unknown role: " + r);
      }
    }

    try {
      user.setPassword(PasswordStorage.createHash(user.getPassword()));

    } catch (final CannotPerformOperationException e) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn("Could not create user due to password hashing failure: " + e.getMessage());
      }
      throw new InternalServerErrorException(e);
    }


    try {
      return this.userCrudService.saveNewEntity(user)
          .orElseThrow(() -> new InternalServerErrorException());
    } catch (final DuplicateKeyException ex) {
      throw new BadRequestException("User already exists", ex);
    } catch (final MongoException ex) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error(MSG_USER_NOT_RETRIEVED + ex.getMessage() + " (" + ex.getCode() + ")");
      }
      throw new InternalServerErrorException(ex);
    }
  }


  /**
   * Creates all users in a list.
   *
   * @param users the list of users to create
   * @return a list of users objects, that were saved
   */
  @POST
  @Consumes(MEDIA_TYPE)
  @Produces(MEDIA_TYPE)
  @Path("batch") // Todo: Find more suitable path
  @RolesAllowed({ADMIN_ROLE})
  public List<User> createAll(final List<User> users) {
    /*
     * Currently, if a user object in the given list does not survive input validation, it will be
     * ignored. No error will be given to the caller, since json api does not allow data and error
     * in one response.
     *
     */

    final List<User> createdUsers = new ArrayList<>();
    for (final User u : users) {
      try {
        createdUsers.add(this.newUser(u));
      } catch (final BadRequestException ex) {
        // Do nothing
        continue;
      }
    }

    return createdUsers;
  }

  /**
   * Updates the details of a already existing user. The values of the targeted user will be
   * overridden by the values of {@linkplain updatedUser}. All attributes that are {@code null} are
   * ignored.
   *
   * @param id the id of the user to update
   * @param updatedUser a {@link User} object containing the changes. All fields set to {@code null}
   *        will be ignored when updating.
   * @return the updated user
   */
  @PATCH
  @Path("{id}")
  @RolesAllowed({ADMIN_ROLE})
  @Produces(MEDIA_TYPE)
  @Consumes(MEDIA_TYPE)
  public User updateUser(@PathParam("id") final Long id, final User updatedUser) { // NOPMD

    User targetUser = null;
    try {
      targetUser =
          this.userCrudService.getEntityById(id).orElseThrow(() -> new NotFoundException());
    } catch (final MongoException ex) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error(MSG_USER_NOT_RETRIEVED + ex.getMessage() + " (" + ex.getCode() + ")");
      }
      throw new InternalServerErrorException(ex);
    }


    if (updatedUser.getId() != null || updatedUser.getId() != id) { // NOPMD
      LOGGER.info("Won't update id");
    }

    if (updatedUser.getPassword() != null) {
      if (updatedUser.getPassword().equals("")) {
        throw new BadRequestException(MSG_INVALID_PASSWORD);
      }
      try {
        targetUser.setPassword(PasswordStorage.createHash(updatedUser.getPassword()));
      } catch (final CannotPerformOperationException e) {
        if (LOGGER.isWarnEnabled()) {
          LOGGER.warn("Could not update user due to password hashing failure: " + e.getMessage());
        }
        throw new InternalServerErrorException(e);
      }
    }

    if (updatedUser.getUsername() != null) {
      if (updatedUser.getUsername().equals("")) {
        throw new BadRequestException(MSG_INVALID_USERNAME);
      }

      targetUser.setUsername(updatedUser.getUsername());
    }

    if (updatedUser.getRoles() != null) {
      for (final Role r : updatedUser.getRoles()) {
        if (!this.roleService.getAllRoles().contains(r)) {
          throw new BadRequestException("Unknown role: " + r);
        }
      }
      targetUser.setRoles(updatedUser.getRoles());
    }

    if (updatedUser.getSettings() != null) {

      try {
        updatedUser.getSettings().validate();
      } catch (final IllegalStateException e) {
        throw new BadRequestException(e.getMessage());
      }
      targetUser.setSettings(updatedUser.getSettings());
    }

    try {
      this.userCrudService.updateEntity(targetUser);
    } catch (final DuplicateKeyException ex) {
      throw new BadRequestException("Username already exists", ex);
    } catch (final MongoException ex) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error(MSG_USER_NOT_UPDATED + ex.getMessage() + " (" + ex.getCode() + ")");
      }
      throw new InternalServerErrorException(ex);
    }

    return targetUser;

  }

  /**
   * Retrieves all users that have a specific role.
   *
   * @param role the role to be searched for
   * @return a list of all users with the given role
   */
  @GET
  @RolesAllowed({ADMIN_ROLE})
  @Produces(MEDIA_TYPE)
  public List<User> usersByRole(@QueryParam("role") final String role) {
    try {

      // Return all users if role parameter is omitted
      if (role == null) {
        return this.userCrudService.getAll();
      }
      return this.userCrudService.getUsersByRole(role);

    } catch (final MongoException ex) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error(MSG_USER_NOT_UPDATED + ex.getMessage() + " (" + ex.getCode() + ")");
      }
      throw new InternalServerErrorException(ex);
    }
  }

  /**
   * Retrieves a single user identified by its id.
   *
   * @param id the id of the user to return
   * @return the {@link User} object with the given id
   */
  @GET
  @Path("{id}")
  @RolesAllowed({ADMIN_ROLE})
  @Produces(MEDIA_TYPE)
  public User userById(@PathParam("id") final Long id) {
    if (id == null || id <= 0) {
      throw new BadRequestException("Id must be positive integer");
    }

    User foundUser = null;

    try {
      foundUser = this.userCrudService.getEntityById(id).orElseThrow(() -> new NotFoundException());
    } catch (final MongoException ex) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error("Could not retrieve user: " + ex.getMessage() + " (" + ex.getCode() + ")");
      }
      throw new InternalServerErrorException(ex);
    }


    if (DefaultSettings.addMissingDefaults(foundUser.getSettings())) {
      this.userCrudService.updateEntity(foundUser);
    }

    return foundUser;

  }

  /**
   * Removes the user with the given id.
   *
   * @param id the id of the user to delete
   */
  @DELETE
  @Path("{id}")
  @RolesAllowed({ADMIN_ROLE})
  public Response removeUser(@PathParam("id") final Long id) {
    try {
      this.userCrudService.deleteEntityById(id);
    } catch (final MongoException ex) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error("Could not update user: " + ex.getMessage() + " (" + ex.getCode() + ")");
      }
      throw new InternalServerErrorException(ex);
    }

    return Response.status(HttpStatus.NO_CONTENT_204).build();
  }

  /**
   * Returns the settings of a user with a given id.
   *
   * Currently unused.
   *
   * @param id the user id
   * @return the {@link UserSettings}
   */
  // @GET
  @Path("settings/{id}")
  @PermitAll
  @Produces(MEDIA_TYPE)
  public UserSettings getSettingsForUser(@PathParam("id") final long id,
      @Context final HttpHeaders headers) {


    final User u = this.userCrudService.getEntityById(id)
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
      this.userCrudService.updateEntity(u);
    }

    return settings;

  }

}
