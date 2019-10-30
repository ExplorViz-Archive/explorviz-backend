package net.explorviz.security.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import net.explorviz.shared.security.filters.Secure;
import net.explorviz.security.user.Role;

/**
 * Provides endpoints for user roles.
 *
 */
@Path("v1/roles")
@Tag(name = "Role")
@SecurityRequirement(name = "token")
@Secure
public class RoleResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  @GET
  @RolesAllowed({Role.ADMIN_NAME})
  @Produces(MEDIA_TYPE)
  @Operation(description = "Returns a list of all available roles")
  @ApiResponse(responseCode = "200", description = "List of all roles currently available.",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = Role.class))))
  public List<Role> getAllRoles() {
    return Role.ROLES;
  }

}
