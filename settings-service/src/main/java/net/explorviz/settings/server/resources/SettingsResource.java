package net.explorviz.settings.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import net.explorviz.security.user.Role;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.services.SettingsRepository;
import net.explorviz.shared.querying.Query;
import net.explorviz.shared.querying.QueryResult;
import net.explorviz.shared.security.filters.Secure;


/**
 * API for handling {@link Setting}s and their associated information.
 *
 */
@Path("v1/settings")
@Tag(name = "Settings")
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "token", scheme = "bearer",
    bearerFormat = "JWT")
@SecurityRequirement(name = "token")
@Secure
public class SettingsResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";


  private final SettingsRepository repo;

  @Inject
  public SettingsResource(final SettingsRepository repo) {
    this.repo = repo;
  }

  /**
   * Endpoint to access all settings.
   *
   * @return all settings currently available.
   */
  @GET
  @Produces(MEDIA_TYPE)
  @Operation(summary = "Find all settings")
  @ApiResponse(description = "Responds with an array of all available settings.",
      responseCode = "200",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = Setting.class))))
  @Parameters({
      @Parameter(in = ParameterIn.QUERY, name = "page[size]",
          description = "Controls the size, i.e., amount of entities, of each page."),
      @Parameter(in = ParameterIn.QUERY, name = "page[number]",
          description = "Index of the page to return."),
      @Parameter(in = ParameterIn.QUERY, name = "filter[origin]",
          description = "Only return settings that were created by the specified origin."),
      @Parameter(in = ParameterIn.QUERY, name = "filter[type]",
          description = "The response only contains settings of the specified type, "
              + "matching the JSON:API type")})
  @PermitAll
  public QueryResult<Setting> getAll(@Context final UriInfo uriInfo) {
    final Query<Setting> query = Query.fromParameterMap(uriInfo.getQueryParameters(true));

    return this.repo.query(query);
  }

  /**
   * Endpoint to access a single setting.
   *
   * @param id the id of the setting
   * @return the setting with HTTP 200 (OK)
   * @throws NotFoundException if there is no setting with the given id
   */
  @GET
  @Produces(MEDIA_TYPE)
  @Path("{id}")
  @Operation(summary = "Find a single setting")
  @ApiResponse(description = "Responds with the requestes settings.", responseCode = "200",
      content = @Content(schema = @Schema(implementation = Setting.class)))
  @ApiResponse(description = "No setting with such id.", responseCode = "404")
  @PermitAll
  public Setting getById(@Parameter(description = "Id of the setting",
      required = true) @PathParam("id") final String id) {
    return this.repo.find(id).orElseThrow(NotFoundException::new);
  }

  /**
   * Endpoint to delete a single setting.
   *
   * @param id the id of the setting
   * @return returns a HTTP Status 201 (No Content).
   */
  @DELETE
  @Produces(MEDIA_TYPE)
  @Path("{id}")
  @RolesAllowed({Role.ADMIN_NAME})
  @Operation(summary = "Delete a setting")
  @ApiResponse(description = "Setting with given id does not exist (anymore)", responseCode = "204")
  public Response deleteById(
      @Parameter(description = "Id of the setting to delete") @PathParam("id") final String id) {
    this.repo.delete(id);
    // 204
    return Response.noContent().build();
  }

  /**
   * Endpoint for creation of new settings.
   *
   * @param s a setting more specific a subtype of {@link Setting}. @return Returns HTTP 200 (OK) on
   *        success
   * @throws BadRequestException if the setting is malformed and could not be saved
   */
  @POST
  @Consumes(MEDIA_TYPE)
  @RolesAllowed({Role.ADMIN_NAME})
  @Operation(summary = "Creat a new setting")
  @ApiResponse(description = "Setting created, response contains the created setting.",
      responseCode = "200", content = @Content(schema = @Schema(implementation = Setting.class)))
  @ApiResponse(responseCode = "400", description = "Setting to create contains invalid values.")
  @RequestBody(content = @Content(schema = @Schema(implementation = Setting.class)),
      description = "Setting to create. The id must not be set.")
  public Setting createSetting(final Setting s) {
    try {
      final Setting updated = this.repo.createOrUpdate(s);
      return updated;
    } catch (final Exception e) {
      throw new BadRequestException(e.getMessage());
    }

  }


}
