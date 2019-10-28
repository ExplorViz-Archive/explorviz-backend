package net.explorviz.history.server.resources;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.history.repository.persistence.ReplayRepository;
import net.explorviz.history.repository.persistence.mongo.LandscapeSerializationHelper;
import net.explorviz.history.util.ResourceHelper;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.security.user.Role;
import net.explorviz.shared.security.filters.Secure;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Resource providing persisted {@link Landscape} data for the frontend.
 */
@Path("v1/landscapes")
@RolesAllowed({Role.ADMIN_NAME, Role.USER_NAME})
@Tag(name = "Landscapes")
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "token", scheme = "bearer",
    bearerFormat = "JWT")
@SecurityRequirement(name = "token")
@Secure
public class LandscapeResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeResource.class);

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final long QUERY_PARAM_DEFAULT_VALUE_LONG = 0L;
  private static final String QUERY_PARAM_EMPTY_STRING = "";

  private final LandscapeRepository<String> landscapeStringRepo;
  private final ReplayRepository<String> replayStringRepo;

  private final LandscapeSerializationHelper serializationHelper;

  @Inject
  public LandscapeResource(final LandscapeRepository<String> landscapeStringRepo,
      final ReplayRepository<String> replayStringRepo,
      final LandscapeSerializationHelper serializationHelper) {
    this.landscapeStringRepo = landscapeStringRepo;
    this.replayStringRepo = replayStringRepo;
    this.serializationHelper = serializationHelper;
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
   * Returns {@link Landscape} with the passed query parameter.
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

  /**
   * Provides a json file for downloading a landscape from the frontend
   *
   * @param timestamp of the {@link Landscape} to return
   * @return the {@link Landscape} as a json file with the given timestamp
   *
   */
  @GET
  @Path("/download")
  @Produces("application/json")
  @Operation(summary = "Download a landscape by its timestamp")
  @ApiResponse(responseCode = "200",
      description = "Response contains the first landscape with the given timestamp.",
      content = @Content(schema = @Schema(implementation = Landscape.class)))
  @ApiResponse(responseCode = "404", description = "No landscape with the given timestamp.")
  public String downloadLandscape(@Parameter(description = "The timestamp to filter by.",
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

  /**
   * Accepts uploading a landscape from the frontend
   *
   * @param uploadedInputStream json-file of the uploaded landscape
   * @param fileInfo information of the file, e.g., the filename
   */
  @POST
  @Path("/replay/upload")
  @Consumes("multipart/form-data")
  @Produces(MEDIA_TYPE)
  @Operation(summary = "Upload a landscape file from the frontend")
  @ApiResponse(responseCode = "200", description = "Response contains the uploaded landscape file.",
      content = @Content(schema = @Schema(implementation = Landscape.class)))
  @ApiResponse(responseCode = "404", description = "Landscape file could not be uploaded.")
  public String uploadLandscape(
      @Parameter(description = "The name of the file.",
          required = true) @QueryParam("filename") final String fileName,
      @Parameter(description = "The uploaded landscape file.",
          required = true) @FormDataParam("file") final InputStream uploadedInputStream,
      @Parameter(description = "The file information of the uploaded landscape.",
          required = true) @FormDataParam("file") final FormDataContentDisposition fileInfo) {

    // TODO check for empty uploaded landscape file
    if (fileName == QUERY_PARAM_EMPTY_STRING) {
      throw new BadRequestException("Query parameter 'filename' is mandatory");
    }

    LOGGER.info("Uploaded Filename: " + fileName);

    // split the passed filename
    final String fileNameWithoutExtension = ResourceHelper.removeFileNameExtension(fileName);
    final String[] splittedFilename = fileNameWithoutExtension.split("-");
    final long parsedTimestamp = Long.valueOf(splittedFilename[0]);

    // check if landscape already exists in `replay` landscape repository
    final String found = Stream.of(this.replayStringRepo.getByTimestamp(parsedTimestamp))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElse(null);

    // landscape not persisted yet => persist in mongoDB and return it afterwards
    if (found == null) {
      Landscape parsedLandscape = null;

      final String convertedInputStream =
          ResourceHelper.convertInputstreamToString(uploadedInputStream);

      try {
        parsedLandscape = this.serializationHelper.deserialize(convertedInputStream);
        this.replayStringRepo.save(parsedLandscape.getTimestamp().getTimestamp(),
            parsedLandscape,
            parsedLandscape.getTimestamp().getTotalRequests());
      } catch (final DocumentSerializationException e) {
        LOGGER.error("Could not save landscape with value {}", parsedLandscape, e);
      }

      // if upload was successful, check for existence in replayRepo or throw Exception
      // this can be done better since Java 9
      return Stream.of(this.replayStringRepo.getByTimestamp(parsedTimestamp))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .findFirst()
          .orElseThrow(() -> new NotFoundException(
              "Uploaded landscape with timestamp " + parsedTimestamp + " not found."));
    } else {
      // throw new NotFoundException("Landscape with timestamp " + parsedTimestamp + " already
      // exists.");
      return Stream.of(this.replayStringRepo.getByTimestamp(parsedTimestamp))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .findFirst()
          .orElseThrow(() -> new NotFoundException(
              "Uploaded landscape with timestamp " + parsedTimestamp + " not found."));
    }

  }

}
