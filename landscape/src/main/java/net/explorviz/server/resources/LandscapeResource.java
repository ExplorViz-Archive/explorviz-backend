package net.explorviz.server.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.api.ExtensionAPI;
import net.explorviz.api.ExtensionAPIImpl;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.repository.RepositoryStorage;
import net.explorviz.server.helper.FileSystemHelper;
import net.explorviz.server.main.Configuration;
import net.explorviz.server.security.Secured;

/**
 * REST resource providing landscape data for the frontend.
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 */
@Secured
@Path("landscape")
public class LandscapeResource {
	static final Logger LOGGER = LoggerFactory.getLogger(LandscapeResource.class.getName());

	private final ExtensionAPIImpl api = ExtensionAPI.get();

	@Produces("application/vnd.api+json")
	@GET
	@Path("/by-timestamp/{timestamp}")
	public Landscape getLandscape(@PathParam("timestamp") final long timestamp) throws FileNotFoundException {
		return api.getLandscape(timestamp, Configuration.LANDSCAPE_REPOSITORY);
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/latest-landscape")
	public Landscape getLatestLandscape() throws FileNotFoundException, IOException {
		return api.getLatestLandscape();
	}

	/**
	 * For downloading a landscape from the landscape repository.
	 */
	@Produces("*/*")
	@GET
	@Path("/export/{fileName}")
	public Response getExportLandscape(@PathParam("fileName") final String fileName) throws FileNotFoundException {

		final String landscapeFolder = FileSystemHelper.getExplorVizDirectory() + File.separator
				+ Configuration.LANDSCAPE_REPOSITORY;

		final Landscape landscapeWithNewIDs = RepositoryStorage.readFromFileGeneric(landscapeFolder,
				fileName + ".expl");

		final byte[] landscapeAsBytes = RepositoryStorage.convertLandscapeToBytes(landscapeWithNewIDs);

		final String encodedLandscape = Base64.getEncoder().encodeToString(landscapeAsBytes);

		// send encoded landscape
		return Response.ok(encodedLandscape, "*/*").build();
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/by-uploaded-timestamp/{timestamp}")
	public Landscape getReplayLandscape(@PathParam("timestamp") final long timestamp) throws FileNotFoundException {
		return api.getLandscape(timestamp, Configuration.REPLAY_REPOSITORY);
	}

	/**
	 * For uploading a landscape to the replay repository.
	 */
	// https://stackoverflow.com/questions/25797650/fileupload-with-jax-rs
	@Consumes("multipart/form-data")
	@POST
	@Path("/upload-landscape")
	public Response uploadLandscape(@FormDataParam("file") final InputStream uploadedInputStream,
			@FormDataParam("file") final FormDataContentDisposition fileInfo) {

		final String baseFilePath = FileSystemHelper.getExplorVizDirectory() + File.separator;
		final String replayFilePath = baseFilePath + Configuration.REPLAY_REPOSITORY + File.separator;

		new File(replayFilePath).mkdir();
		final File objFile = new File(replayFilePath + fileInfo.getFileName());
		if (objFile.exists()) {
			objFile.delete();

		}

		saveToFile(uploadedInputStream, replayFilePath + fileInfo.getFileName());

		return Response.ok().build();
	}

	private void saveToFile(final InputStream uploadedInputStream, final String uploadedFileLocation) {
		// decode and save landscape
		try (InputStream base64is = Base64.getDecoder().wrap(uploadedInputStream)) {
			int len = 0;
			OutputStream out = null;
			final byte[] bytes = new byte[1024];
			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((len = base64is.read(bytes)) != -1) {
				out.write(bytes, 0, len);
			}
			out.flush();
			out.close();
		} catch (final IOException e1) {
			LOGGER.error(
					"Replay landscape could not be saved to replay repository. Error {} occured. With stacktrace {}",
					e1.getMessage(), e1.getStackTrace());

		}

	}
}
