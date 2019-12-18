package net.explorviz.history.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import net.explorviz.history.repository.persistence.mongo.TimestampRepository;
import net.explorviz.landscape.model.store.Timestamp;
import net.explorviz.security.user.Role;
import net.explorviz.shared.querying.Query;
import net.explorviz.shared.querying.QueryException;
import net.explorviz.shared.querying.QueryResult;
import net.explorviz.shared.security.filters.Secure;


/**
 * REST resource providing {@link net.explorviz.landscape.model.store.Timestamp} data for the
 * frontend.
 */
@Path("v1/timestamps")
@RolesAllowed({Role.ADMIN_NAME})
@SecurityRequirement(name = "token")
@Tag(name = "Timestamps")
@Secure
public class TimestampResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";


  private final TimestampRepository timestampRepo;

  @Inject
  public TimestampResource(final TimestampRepository timestampRepo) {
    this.timestampRepo = timestampRepo;
  }


  /**
   * Returns a list of either user-uploaded or service-generated
   * {@link net.explorviz.landscape.model.store.Timestamp}. The result depends on the passed query
   * parameters
   *
   * @return a filtered list of timestamps
   */
  @GET
  @Produces(MEDIA_TYPE)
  @Operation(summary = "Find a range of timestamps")
  @ApiResponse(responseCode = "200",
      description = "Response contains the timestamp satisfying the applied filters.",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = Timestamp.class))))
  @ApiResponse(responseCode = "400", description = "Invalid query parameters")
  @Parameters({
      @Parameter(in = ParameterIn.QUERY, name = "page[size]",
          description = "Controls the size, i.e., amount of entities, of each page."),
      @Parameter(in = ParameterIn.QUERY, name = "page[number]",
          description = "Index of the page to return."),
      @Parameter(in = ParameterIn.QUERY, name = "filter[type]",
          description = "Response only contains the given type ({landscape, replay})."),
      @Parameter(in = ParameterIn.QUERY, name = "filter[from]",
          description = "Lower bound for the timestamp to return."),
      @Parameter(in = ParameterIn.QUERY, name = "filter[to]",
          description = "Upper bound for the timestamp to return.")})
  public QueryResult<Timestamp> getTimestamps(@Context final UriInfo uriInfo) {
    final Query<Timestamp> q = Query.fromParameterMap(uriInfo.getQueryParameters(true));
    try {
      return this.timestampRepo.query(q);
    } catch (final QueryException e) {
      throw new BadRequestException(e);
    }
  }


}
