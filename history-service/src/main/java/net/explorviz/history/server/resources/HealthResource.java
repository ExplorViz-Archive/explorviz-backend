package net.explorviz.history.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Health check for this service.
 */
@Path("v1/health")
@Tag(name = "Health")
public class HealthResource {

  /**
   * This method provides a health check for the service.
   */
  @GET
  @PermitAll
  @Operation(description = "Used for a health check of this service.")
  @ApiResponse(responseCode = "200", description = "Simple health check.")
  public String showEntryPoint() {

    return "Healthy";
  }


}
