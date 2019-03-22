package net.explorviz.security.server.resources;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import net.explorviz.security.services.RoleService;
import net.explorviz.shared.security.model.roles.Role;

/**
 * Provides endpoints for user roles.
 *
 */
@Path("v1/roles")
public class RoleResource {

  // private static final Logger LOGGER = LoggerFactory.getLogger(RoleResource.class); // NOPMD

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String ADMIN_ROLE = "admin";

  @Inject
  private RoleService roleService;

  @GET
  @RolesAllowed({ADMIN_ROLE})
  @Produces(MEDIA_TYPE)
  public List<Role> getAllRoles() {
    return this.roleService.getAllRoles();
  }

}
