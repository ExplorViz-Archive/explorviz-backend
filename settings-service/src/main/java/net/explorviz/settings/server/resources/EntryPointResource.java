package net.explorviz.settings.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Entry point for this service.
 */
@Path("v1/settings/entry")
@Tag(name = "Entry")
public class EntryPointResource {

  /**
   * This method is the entry point for this service and returns (in the future) a HATEOAS-based
   * payload for clients.
   */
  @GET
  @PermitAll
  @Operation(description = "Show this API's entry point.")
  @ApiResponse(responseCode = "200", description = "Returns the entry point for this API, "
      + "so that users can follow the included links in terms of HATEOAS.")
  public String showEntryPoint() {

    return "Entry Point";
  }


}
