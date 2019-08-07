package net.explorviz.history.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.history.repository.persistence.ReplayRepository;
import net.explorviz.shared.landscape.model.landscape.Landscape;


/**
 * Resource providing persisted {@link Landscape} data for the frontend.
 */
@Path("v1/landscapes")
@RolesAllowed({"admin", "user"})
@Tag(name = "Landscapes")
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "token", scheme = "bearer",
    bearerFormat = "JWT")
@SecurityRequirement(name = "token")
public class LandscapeResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final long QUERY_PARAM_DEFAULT_VALUE_LONG = 0L;

  private final LandscapeRepository<String> landscapeStringRepo;
  private final ReplayRepository<String> replayStringRepo;


  @Inject
  public LandscapeResource(final LandscapeRepository<String> landscapeStringRepo,
      final ReplayRepository<String> replayStringRepo) {
    this.landscapeStringRepo = landscapeStringRepo;
    this.replayStringRepo = replayStringRepo;
  }

  // akr: IMHO best option for decision between 404 or 200 Empty
  // https://stackoverflow.com/a/48746789

  /**
   * Returns a landscape by its id or 404.
   *
   * @param id - entity id of the landscape
   * @return landscape object found by passed id or 404.
   */
  @GET
  @Path("{id}")
  @Produces(MEDIA_TYPE)
  @Operation(summary = "Find a landscape by its id")
  @ApiResponse(responseCode = "200", description = "Response contains the requested landscape.",
      content = @Content(schema = @Schema(implementation = Landscape.class)))
  @ApiResponse(responseCode = "404", description = "No landscape with such id.")
  public String getLandscapeById(@Parameter(description = "Id of the landscape",
      required = true) @PathParam("id") final String id) {

    // Check existence in landscapeRepo and replayRepo or throw Exception
    // this can be done better since Java 9
    return Stream.of(this.landscapeStringRepo.getById(id), this.replayStringRepo.getById(id))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseThrow(() -> new NotFoundException("Landscape with id " + id + " not found.")); // NOCS
  }

  /**
   * Returns {@link net.explorviz.shared.landscape.model.landscape.Landscape} with the passed query
   * parameter.
   *
   * @param timestamp - query parameter
   * @return the requested timestamp
   */
  @GET
  @Produces(MEDIA_TYPE)
  @Operation(summary = "Find a landscape by its timestamp")
  @ApiResponse(responseCode = "200",
      description = "Response contains the first landscape with the given timestamp.",
      content = @Content(schema = @Schema(implementation = Landscape.class)))
  @ApiResponse(responseCode = "404", description = "No landscape with the given timestamp.")
  public String getLandscape(@Parameter(description = "The timestamp to filter by.",
      required = true) @QueryParam("timestamp") final long timestamp) {

    if (timestamp == QUERY_PARAM_DEFAULT_VALUE_LONG) {
      throw new BadRequestException("Query parameter 'timestamp' is mandatory");
    }

    // Check existence in landscapeRepo and replayRepo or throw Exception
    // this can be done better since Java 9
    return Stream
        .of(this.landscapeStringRepo.getByTimestamp(timestamp),
            this.replayStringRepo.getByTimestamp(timestamp))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseThrow(
            () -> new NotFoundException("Landscape with timestamp " + timestamp + " not found."));
  }

}
