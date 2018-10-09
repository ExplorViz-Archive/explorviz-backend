package net.explorviz.security.server.resources;

import java.util.List;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import net.explorviz.security.services.UserCrudService;
import net.explorviz.shared.annotations.Secured;
import net.explorviz.shared.security.User;

/**
 * Provides endpoints for user management.
 *
 * @author Kevin Lotz
 *
 */
@Path("v1/users")
public class UserResource {

  @Inject
  private UserCrudService userService;


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

    return null;
  }

  /**
   * Retrieves all users that have a specific role.
   *
   * @param role the role to be searched for
   * @return all users with the given role
   */
  @GET
  @Secured
  @Produces(MediaType.APPLICATION_JSON)
  public List<User> userByRole(@QueryParam("role") final String role) {


    return null;
  }


  /**
   * Removes the user with the given id.
   *
   * @param id the id of the user to delete
   */
  @DELETE
  @Path("{id}")
  @Secured
  public void removeUser(@PathParam("id") final Long id) {



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
  public void changePassword(@PathParam("id") final Long id, final String password) {


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

    return null;
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
