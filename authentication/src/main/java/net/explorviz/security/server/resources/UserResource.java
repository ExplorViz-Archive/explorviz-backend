package net.explorviz.security.server.resources;

import java.util.ArrayList;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import net.explorviz.security.services.UserCrudService;
import net.explorviz.security.util.PasswordStorage;
import net.explorviz.security.util.PasswordStorage.CannotPerformOperationException;
import net.explorviz.shared.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides endpoints for user management.
 *
 */
@Path("v1/users")
public class UserResource {

  @Inject
  private UserCrudService userCrudService;



  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);



  @Produces(MEDIA_TYPE)
  @RolesAllowed({"admin"})
  public List<User> allUsers() {

    return this.userCrudService.getAll();
  }

  /**
   * Creates and persists a new user.
   *
   * @param user the user to create
   * @return the user object, that was saved
   */
  @POST
  @Consumes(MEDIA_TYPE)
  @Produces(MEDIA_TYPE)
  @RolesAllowed({"admin"})
  public User newUser(final User user) {

    if (user.getUsername() == null || user.getUsername().equals("")) {
      throw new BadRequestException("Invalid username");
    }

    if (user.getPassword() == null || user.getPassword().equals("")) {
      throw new BadRequestException("Invalid password");
    }

    if (user.getId() != null && user.getId() >= 0) {
      throw new BadRequestException("Can't create user with existing id");
    }

    try {
      user.setPassword(PasswordStorage.createHash(user.getPassword()));

      // Generate new id
      user.setId(null);
    } catch (final CannotPerformOperationException e) {
      LOGGER.warn("Could not create user due to password hashing failure: " + e.getMessage());
      throw new InternalServerErrorException();
    }

    return this.userCrudService.saveNewUser(user);
  }


  /**
   * Creates all users in a list
   *
   * @param users the list of users to create
   * @return a list of users objects, that were saved
   */
  @POST
  @Consumes(MEDIA_TYPE)
  @Produces(MEDIA_TYPE)
  @Path("batch") // Todo: Find more suitable path
  @RolesAllowed({"admin"})
  public List<User> createAll(final List<User> users) {
    /*
     * Currently, if a user object in the given list does not survive input validation, it will be
     * ignored. No error will be given to the caller, since json api does not allow data and error
     * in one response.
     *
     * I don't know if this is the preferred behavior.
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
  @RolesAllowed({"admin"})
  @Produces(MEDIA_TYPE)
  @Consumes(MEDIA_TYPE)
  public User updateUser(@PathParam("id") final Long id, final User updatedUser) {
    final User targetUser = this.userCrudService.getUserById(id);

    if (targetUser == null) {
      throw new NotFoundException();
    }

    if (updatedUser.getId() != null) {
      throw new BadRequestException("Can't update id");
    }

    if (updatedUser.getPassword() != null) {
      if (updatedUser.getPassword().equals("")) {
        throw new BadRequestException("Invalid password");
      }
      try {
        targetUser.setPassword(PasswordStorage.createHash(updatedUser.getPassword()));
      } catch (final CannotPerformOperationException e) {
        LOGGER.warn("Could not update user due to password hashing failure: " + e.getMessage());
        throw new InternalServerErrorException();
      }
    }

    if (updatedUser.getUsername() != null) {
      if (updatedUser.getUsername().equals("")) {
        throw new BadRequestException("Invalid username");
      }

      targetUser.setUsername(updatedUser.getUsername());
    }

    if (updatedUser.getRoles() != null) {
      targetUser.setRoles(updatedUser.getRoles());
    }

    this.userCrudService.updateUser(targetUser);

    return targetUser;

  }

  /**
   * Retrieves all users that have a specific role.
   *
   * @param role the role to be searched for
   * @return a list of all users with the given role
   */
  @GET
  @RolesAllowed({"admin"})
  @Produces(MEDIA_TYPE)
  public List<User> usersByRole(@QueryParam("role") final String role) {
    return this.userCrudService.getUsersByRole(role);
  }

  /**
   * Retrieves a single user identified by its id.
   *
   * @param id the id of the user to return
   * @return the {@link User} object with the given id
   */
  @GET
  @Path("{id}")
  @RolesAllowed({"admin"})
  @Produces(MEDIA_TYPE)
  public User userById(@PathParam("id") final Long id) {
    if (id == null || id <= 0) {
      throw new BadRequestException("Id must be positive integer");
    }
    final User foundUser = this.userCrudService.getUserById(id);

    if (foundUser == null) {
      throw new NotFoundException();
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
  @RolesAllowed({"admin"})
  public Response removeUser(@PathParam("id") final Long id) {
    this.userCrudService.deleteUserById(id);

    return Response.status(204).build();
  }

}
