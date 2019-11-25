package net.explorviz.history.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.landscape.System;
import net.explorviz.shared.security.filters.Secure;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.Optional;

/**
 * Serves {@link System}s associated to a landscape.
 *
 * {@code /v1/landscapes/<id>}
 */
@Secure
@Tag(name = "systems")
@SecurityScheme(type = SecuritySchemeType.HTTP,
    name = "token",
    scheme = "bearer",
    bearerFormat = "JWT")
@SecurityRequirement(name = "token")
public class SystemSubResource {

    private static final String MEDIA_TYPE = "application/vnd.api+json";

    // The landscape in question
    private Landscape landscape;

    public SystemSubResource(Landscape landscape) {
        this.landscape = landscape;
    }

    /**
     *
     * @return All systems associated to the landscape
     */
    @GET
    @Operation(summary = "Find all systems associated to a landscape")
    @ApiResponse(responseCode = "200",
        description = "Response contains the systems.",
        content = @Content(schema = @Schema(implementation = System.class)))
    @PermitAll
    public List<System> getAll() {
        return landscape.getSystems();
    }

    /**
     *
     * @param sid the id of the system
     * @return the system with the given id
     */
    @Path("{sid}")
    @GET
    @Operation(summary = "Get a specific systems of a landscape")
    @ApiResponse(responseCode = "200",
        description = "Response contains the queried system.",
        content = @Content(schema = @Schema(implementation = System.class)))
    @PermitAll
    public System getById(@PathParam("sid") @Parameter(description = "Id of the system",
        required = true) String sid) {

        // TODO: There are systems with null ids!?

        java.lang.System.out.println("system id:" + sid);

        Optional<System> system = landscape.getSystems().stream()
            .filter(s -> s.getId() != null && s.getId().contentEquals(sid)).findFirst();
        return system.orElseThrow(NotFoundException::new);

    }


}
