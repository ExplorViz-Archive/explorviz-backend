package net.explorviz.security.server.resources;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import net.explorviz.security.services.UserService;
import net.explorviz.security.services.exceptions.DuplicateUserException;
import net.explorviz.security.services.exceptions.UserCrudException;
import net.explorviz.security.user.User;
import net.explorviz.security.util.PasswordStorage;
import net.explorviz.security.util.PasswordStorage.CannotPerformOperationException;
import net.explorviz.shared.querying.Query;
import net.explorviz.shared.querying.QueryResult;
import net.explorviz.security.user.Role;
import net.explorviz.shared.security.filters.Secure;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides several Endpoints for user management.
 *
 */
@Path("v1/users")
@Tag(name = "User")
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "token", scheme = "bearer",
    bearerFormat = "JWT")
@SecurityRequirement(name = "token")
@Secure
public class UserResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

  private static final String MSG_INVALID_PASSWORD = "Invalid password";
  private static final String MSG_INVALID_USERNAME = "Invalid username";
  private static final String MSG_USER_NOT_RETRIEVED = "Could not retrieve user ";
  private static final String MSG_UNKOWN_ROLE = "Unknown role";


  private final UserService userCrudService;


  private final BatchRequestResource batchSubResource;

  /**
   * Constructor for this class.
   *
   * @param userCrudService - Service to obtain actual users.
   * @param batchSubResource - Sub Resource Class.
   */
  @Inject
  public UserResource(final UserService userCrudService,
      final BatchRequestResource batchSubResource) {
    this.userCrudService = userCrudService;
    this.batchSubResource = batchSubResource;
  }

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
  @RolesAllowed({Role.ADMIN_NAME})
  @Operation(summary = "Create a new user")
  @ApiResponse(responseCode = "200", description = "User created",
      content = @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "400",
      description = "Invalid properties for the user or user with the given name already exists.")
  @RequestBody(
      description = "The user to be created. "
          + "Both the password and the name must not be empty. "
          + "No user must exist with the given name. "
          + "The id must not be set, i.e., must be null." + " The specified roles must exist.",
      content = @Content(schema = @Schema(implementation = User.class)))
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

    for (final String r : user.getRoles()) {
      if (!Role.exists(r)) {
        throw new BadRequestException(MSG_UNKOWN_ROLE + ": " + r);
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
      return this.userCrudService.saveNewEntity(user);
    } catch (final DuplicateUserException ex) {
      throw new BadRequestException("User already exists", ex);
    } catch (final UserCrudException ex) {
      LOGGER.error("Error saving user", ex);
      throw new InternalServerErrorException();
    }
  }

  /**
   * Updates the details of a already existing user. The values of the targeted user will be
   * overridden by the values of an updatedUser. All attributes that are {@code null} are ignored.
   *
   * @param id - the id of the user to update
   * @param updatedUser - a {@link User} object containing the changes. All fields set to
   *        {@code null} will be ignored when updating.
   * @return the updated user
   */
  @PATCH
  @Path("{id}")
  @RolesAllowed({Role.ADMIN_NAME})
  @Produces(MEDIA_TYPE)
  @Consumes(MEDIA_TYPE)
  @Operation(summary = "Update an existing User")
  @ApiResponse(responseCode = "200", description = "Updated user",
      content = @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "400", description = "Properties to update are invalid")
  public User updateUser(
      @Parameter(description = "Id of the user to update") @PathParam("id") final String id,
      @Parameter(description = "Updated values") final User updatedUser) { // NOPMD

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


    if (updatedUser.getId() != null && !updatedUser.getId().equals(id)) { // NOPMD
      LOGGER.info("Won't update id");
      throw new BadRequestException("Can't update id, leave null");
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
      for (final String r : updatedUser.getRoles()) {
        if (!Role.exists(r)) {
          throw new BadRequestException("Unknown role: " + r);
        }
      }
      targetUser.setRoles(updatedUser.getRoles());
    }


    try {
      this.userCrudService.updateEntity(targetUser);
    } catch (final DuplicateKeyException ex) {
      throw new BadRequestException("Username already exists", ex);

    }

    return targetUser;

  }

  /**
   * Retrieves all users that have a specific role.
   *
   * @return a list of all users with the given role
   */
  @GET
  @RolesAllowed({Role.ADMIN_NAME})
  @Produces(MEDIA_TYPE)
  @Operation(description = "List all users")
  @Parameters({
      @Parameter(in = ParameterIn.QUERY, name = "page[size]",
          description = "Controls the size, i.e., amount of entities, of each page."),
      @Parameter(in = ParameterIn.QUERY, name = "page[number]",
          description = "Index of the page to return."),
      @Parameter(in = ParameterIn.QUERY, name = "filter[roles]",
          description = "Restricts the result to the given role(s)."),
      @Parameter(in = ParameterIn.QUERY, name = "filter[batchid]",
          description = "Only return users that were created by the batch request "
              + "with the specified id.")})
  @ApiResponse(responseCode = "200", description = "List of users matching the request",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class))))
  public QueryResult<User> find(@Context final UriInfo uri) {
    final Query<User> query = Query.fromParameterMap(uri.getQueryParameters(true));
    return this.userCrudService.query(query);
  }

  /**
   * Retrieves a single user identified by its id.
   *
   * @param id - the id of the user to return
   * @return the {@link User} object with the given id
   */
  @GET
  @Path("{id}")
  @RolesAllowed({Role.ADMIN_NAME, Role.USER_NAME})
  @Produces(MEDIA_TYPE)
  @Operation(summary = "Find a user by its id")
  @ApiResponse(responseCode = "200", description = "The requested user",
      content = @Content(mediaType = MEDIA_TYPE, schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "404", description = "No such user")
  public User userById(
      @Parameter(description = "Unique id of the user to find") @PathParam("id") final String id) {
    if (id == null || "".equals(id)) {
      throw new BadRequestException("Invalid id");
    }

    // TODO if a user is a normal user check if he tries to get just his own data?
    User foundUser = null;
    foundUser = this.userCrudService.getEntityById(id).orElseThrow(() -> new NotFoundException());

    this.userCrudService.updateEntity(foundUser);

    return foundUser;
  }

  /**
   * Removes the user with the given id.
   *
   * @param id - the id of the user to delete
   * @return a response on success
   */
  @DELETE
  @Path("{id}")
  @RolesAllowed({Role.ADMIN_NAME})
  @Operation(summary = "Remove a user identified by its Id")
  @ApiResponse(responseCode = "400",
      description = "Attempt to delete the last existing use with the admin role, "
          + "which is not possible")
  @ApiResponse(responseCode = "204", description = "User deleted")
  public Response removeUser(@Parameter(
      description = "Unique id of the user to delete") @PathParam("id") final String id) {
    try {
      this.userCrudService.deleteEntityById(id);

    } catch (final UserCrudException ex) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Tried to delete last admin");
      }
      throw new BadRequestException(ex);
    }


    return Response.status(HttpStatus.NO_CONTENT_204).build();
  }

  /**
   * Deletes all given users.
   *
   * @param users the users to delete
   * @return 204 if no error occured
   */
  @DELETE
  @RolesAllowed({Role.ADMIN_NAME})
  @Operation(summary = "Delete a list of users")
  @ApiResponse(responseCode = "204", description = "All user deleted")
  @ApiResponse(responseCode = "400",
      description = "Attempt to delete the last existing use with the admin role, "
          + "which is not possible")
  public Response removeAll(final List<User> users) {

    users.forEach(u -> this.removeUser(u.getId()));
    return Response.status(HttpStatus.NO_CONTENT_204).build();
  }


}
