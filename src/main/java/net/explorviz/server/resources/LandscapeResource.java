package net.explorviz.server.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
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
	@Path("/export/{timestamp}")
	public Response getExportLandscape(@PathParam("timestamp") final long timestamp) throws FileNotFoundException {

		final File landscapeRepository = new File(
				FileSystemHelper.getExplorVizDirectory() + File.separator + Configuration.LANDSCAPE_REPOSITORY);

		// retrieve file from landscape repository with specific timestamp
		final File[] filesWithTimestamp = landscapeRepository.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File landscapeRepository, final String filename) {
				return filename.startsWith(Long.toString(timestamp));
			}
		});

		File exportLandscape;

		if (filesWithTimestamp == null) {
			throw new FileNotFoundException("No landscape found with timestamp:" + timestamp);
		} else {
			exportLandscape = new File(filesWithTimestamp[0].getAbsolutePath());
		}

		String encodedLandscape = "";
		// encode to Base64
		try (FileInputStream streamedLandscape = new FileInputStream(exportLandscape)) {
			final byte[] landscapeData = new byte[(int) exportLandscape.length()];
			streamedLandscape.read(landscapeData);
			encodedLandscape = Base64.getEncoder().encodeToString(landscapeData);
		} catch (final IOException ioe) {
			LOGGER.error("error {} in encoding landscape with timestamp {}.", ioe.getMessage(), timestamp);
		}
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
