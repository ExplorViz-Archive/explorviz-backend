package net.explorviz.security.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import net.explorviz.security.services.RoleService;
import net.explorviz.shared.security.model.roles.Role;
import net.explorviz.shared.security.model.roles.RoleNames;

/**
 * Provides endpoints for user roles.
 *
 */
@Path("v1/roles")
@Tag(name = "Role")
public class RoleResource {

  // private static final Logger LOGGER = LoggerFactory.getLogger(RoleResource.class); // NOPMD

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  @Inject
  private RoleService roleService;

  @GET
  @RolesAllowed({RoleNames.ADMIN})
  @Produces(MEDIA_TYPE)
  @Operation(description = "Returns a list of all available roles")
  @ApiResponse(responseCode = "200", description = "List of all roles currently available.",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = Role.class))))
  public List<Role> getAllRoles() {
    return this.roleService.getAllRoles();
  }

}
