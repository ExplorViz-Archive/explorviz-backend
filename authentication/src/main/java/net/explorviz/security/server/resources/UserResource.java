package net.explorviz.security.server.resources;

import java.util.List;
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.explorviz.security.services.UserCrudService;
import net.explorviz.shared.annotations.Secured;
import net.explorviz.shared.security.User;

/**
 * Provides endpoints for user management.
 *
 * @author lotzk
 *
 */
@Path("v1/users")
public class UserResource {

  @Inject
  private UserCrudService userCrudService;


  /**
   * Creates and persists a new user.
   *
   * @param user the user to create
   * @return the user object, that was saved
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PermitAll
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


    final User persistedUser = this.userCrudService.saveNewUser(user);

    // Don't return the password
    persistedUser.setPassword(null);
    return persistedUser;
  }

  /**
   * Retrieves all users that have a specific role. Used for testing purposes only, not exposed.
   *
   * @param role the role to be searched for
   * @return a list of all users with the given role
   */
  // @GET
  // @Secured
  // @Produces(MediaType.APPLICATION_JSON)
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
  @Secured
  @Produces(MediaType.APPLICATION_JSON)
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
  @Secured
  public Response removeUser(@PathParam("id") final Long id) {
    this.userCrudService.deleteUserById(id);

    return Response.status(204).build();
  }

  /**
   * Changes the password of an existing user.
   *
   * @param id the id of the user
   * @param password the new password
   */
  @PUT
  @Path("{id}/password")
  @Consumes(MediaType.APPLICATION_JSON)
  @Secured
  public Response changePassword(@PathParam("id") final Long id, final String password) {
    final User foundUser = this.userCrudService.getUserById(id);

    if (foundUser == null) {
      // Return 403 instead of 400 to not expose existing user ids
      throw new ForbiddenException();
    }

    foundUser.setPassword(password);
    this.userCrudService.updateUser(foundUser);

    return Response.status(403).build();

  }


  /**
   * Retrieves a list of all roles of a user.
   *
   * @param id the id of the user
   * @return a list of all roles that are associated to this user
   */
  @GET
  @Path("{id}/roles")
  @Produces(MediaType.APPLICATION_JSON)
  public List<String> userRoles(@PathParam("id") final Long id) {
    final User foundUser = this.userCrudService.getUserById(id);

    if (foundUser == null) {
      // Return 403 instead of 400 to not expose existing user ids
      throw new ForbiddenException();
    }

    return foundUser.getRoles();
  }


  /**
   * Changes the roles a user
   *
   * @param id the id of the user
   */
  @PATCH
  @Path("{id}/roles")
  @Consumes(MediaType.APPLICATION_JSON)
  public void changeRoles(@PathParam("id") final Long id) {
    // Todo
  }



}
