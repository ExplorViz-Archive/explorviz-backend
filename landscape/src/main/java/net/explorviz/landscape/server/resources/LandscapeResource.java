package net.explorviz.landscape.server.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.Sse;
import net.explorviz.landscape.api.ExtensionApiImpl;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.repository.persistence.RepositoryFileStorage;
import net.explorviz.landscape.server.helper.FileSystemHelper;
import net.explorviz.landscape.server.main.Configuration;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource providing landscape data.
 */
@Path("v1/landscapes")
@RolesAllowed({"admin"})
public class LandscapeResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeResource.class);
  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private final ExtensionApiImpl api;

  @Inject
  public LandscapeResource(final ExtensionApiImpl api) {
    this.api = api;
  }

  @GET
  @Path("{id}")
  @Produces(MEDIA_TYPE)
  public Landscape getLandscapeById(@PathParam("id") final String id) {

    // curl -X GET 'http://localhost:8081/v1/landscapes/1/' -H 'Accept:
    // application/vnd.api+json' -H 'Authorization: Bearer <token>'

    // TODO return Landscape By Id
    return new Landscape();
  }

  @GET
  @Produces(MEDIA_TYPE)
  public Landscape getLandscapeByTimestamp(@QueryParam("timestamp") final long timestamp) {
    return this.api.getLandscape(timestamp, "landscapeRepository");
  }

  @Path("/broadcast")
  public LandscapeBroadcastSubResource getLandscapeBroadcastResource(@Context final Sse sse,
      @Context final LandscapeBroadcastSubResource landscapeBroadcastSubResource) {

    // curl -v -X GET http://localhost:8081/v1/landscapes/broadcast/ -H
    // "Content-Type: text/event-stream" -H 'Authorization: Bearer <token>'

    return landscapeBroadcastSubResource;
  }

  /**
   * For downloading a landscape from the landscape repository.
   *
   * TODO: Use timestamp or id?
   */
  @GET
  @Path("/export/{fileName}")
  @Produces("*/*")
  public Response getExportLandscape(@PathParam("fileName") final String fileName)
      throws FileNotFoundException {

    final String landscapeFolder = FileSystemHelper.getExplorVizDirectory() + File.separator
        + Configuration.LANDSCAPE_REPOSITORY;

    final Landscape landscapeWithNewIDs =
        RepositoryFileStorage.readFromFileGeneric(landscapeFolder, fileName + ".expl");

    final byte[] landscapeAsBytes =
        RepositoryFileStorage.convertLandscapeToBytes(landscapeWithNewIDs);

    final String encodedLandscape = Base64.getEncoder().encodeToString(landscapeAsBytes);

    // send encoded landscape
    return Response.ok(encodedLandscape, "*/*").build();
  }

  @GET
  @Path("/by-uploaded-timestamp/{timestamp}")
  @Produces(MEDIA_TYPE)
  public Landscape getReplayLandscape(@PathParam("timestamp") final long timestamp)
      throws FileNotFoundException {
    return this.api.getLandscape(timestamp, Configuration.REPLAY_REPOSITORY);
  }

  /**
   * For uploading a landscape to the replay repository.
   */
  // https://stackoverflow.com/questions/25797650/fileupload-with-jax-rs
  @POST
  @Path("/upload-landscape")
  @Consumes("multipart/form-data")
  public Response uploadLandscape(@FormDataParam("file") final InputStream uploadedInputStream,
      @FormDataParam("file") final FormDataContentDisposition fileInfo) {

    final String baseFilePath = FileSystemHelper.getExplorVizDirectory() + File.separator;
    final String replayFilePath = baseFilePath + Configuration.REPLAY_REPOSITORY + File.separator;

    new File(replayFilePath).mkdir();
    final File objFile = new File(replayFilePath + fileInfo.getFileName());
    if (objFile.exists()) {
      objFile.delete();

    }

    this.saveToFile(uploadedInputStream, replayFilePath + fileInfo.getFileName());

    return Response.ok().build();
  }

  private void saveToFile(final InputStream uploadedInputStream,
      final String uploadedFileLocation) {
    // decode and save landscape
    try (InputStream base64is = Base64.getDecoder().wrap(uploadedInputStream)) {
      int len = 0;
      final byte[] bytes = new byte[1024];
      try (OutputStream out = Files.newOutputStream(Paths.get(uploadedFileLocation))) {
        while ((len = base64is.read(bytes)) != -1) { // NOPMD
          out.write(bytes, 0, len);
        }
      }
    } catch (final IOException e1) {
      LOGGER.error(
          "Replay landscape could not be saved to replay repository. Error {} occured. With stacktrace {}",
          e1.getMessage(), e1.getStackTrace());

    }

  }
}
