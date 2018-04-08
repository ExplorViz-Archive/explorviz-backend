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

import net.explorviz.api.ExtensionAPI;
import net.explorviz.api.ExtensionAPIImpl;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.server.helper.FileSystemHelper;
import net.explorviz.server.main.Configuration;

/**
 * REST resource providing landscape data for the frontend.
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 */
// @Secured
@Path("landscape")
public class LandscapeResource {

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
	public Landscape getLatestLandscape() {
		return api.getLatestLandscape();
	}

	@Produces("*/*")
	@GET
	@Path("/export/{timestamp}")
	public Response getExportLandscape(@PathParam("timestamp") final long timestamp) throws FileNotFoundException {

		final File landscapeRepository = new File(
				FileSystemHelper.getExplorVizDirectory() + File.separator + Configuration.LANDSCAPE_REPOSITORY);
		System.out.println("landscapeRepository: " + landscapeRepository.getAbsolutePath());
		System.out.printf("timestamp: %d\n", timestamp);
		// https://stackoverflow.com/questions/13515150/how-to-get-file-from-directory-with-pattern-filter
		final File[] filesWithTimestamp = landscapeRepository.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File landscapeRepository, final String filename) {
				System.out.println("filename: " + filename);
				System.out.println("filename that matches: \n" + filename.matches("^" + timestamp + "\\-[0-9]\\.expl"));
				return filename.startsWith(Long.toString(timestamp));
			}
		});
		System.out.println("filesWithTimestamp[0]: " + filesWithTimestamp[0]);
		final File exportLandscape = new File(filesWithTimestamp[0].getAbsolutePath());
		String encodedLandscape = "";
		// http://javasampleapproach.com/java/java-advanced/java-8-encode-decode-an-image-base64
		try (FileInputStream streamedLandscape = new FileInputStream(exportLandscape)) {
			final byte[] landscapeData = new byte[(int) exportLandscape.length()];
			streamedLandscape.read(landscapeData);
			encodedLandscape = Base64.getEncoder().encodeToString(landscapeData);
		} catch (final IOException ioe) {
			System.err.printf("I/O error in encoding landscape: %s%n", ioe.getMessage());
		}

		return Response.ok(encodedLandscape, "*/*").build();
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/by-uploaded-timestamp/{timestamp}")
	public Landscape getReplayLandscape(@PathParam("timestamp") final long timestamp) throws FileNotFoundException {
		return api.getLandscape(timestamp, Configuration.REPLAY_REPOSITORY);
	}

	// https://stackoverflow.com/questions/25797650/fileupload-with-jax-rs
	@Consumes("multipart/form-data")
	@POST
	@Path("/upload-landscape")
	public Response uploadLandscape(@FormDataParam("file") final InputStream uploadedInputStream,
			@FormDataParam("file") final FormDataContentDisposition fileInfo) {

		final String uploadedLandscapeFilePath = FileSystemHelper.getExplorVizDirectory() + File.separator;
		final String replayFilePath = uploadedLandscapeFilePath + Configuration.REPLAY_REPOSITORY + File.separator;
		System.out.println("replay: " + uploadedLandscapeFilePath);

		new File(replayFilePath).mkdir();
		final File objFile = new File(replayFilePath + fileInfo.getFileName());
		if (objFile.exists()) {
			objFile.delete();

		}

		saveToFile(uploadedInputStream, replayFilePath + fileInfo.getFileName());

		// TODO send other response, if sth. went wrong
		return Response.ok().build();
	}

	private void saveToFile(final InputStream uploadedInputStream, final String uploadedFileLocation) {
		// http://javasampleapproach.com/java/java-advanced/java-8-encode-decode-an-image-base64
		try (InputStream base64is = Base64.getDecoder().wrap(uploadedInputStream)) {
			int len = 0;
			OutputStream out = null;
			final byte[] bytes = new byte[1024];
			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((len = base64is.read(bytes)) != -1) {
				// System.out.print(new String(bytes, 0, len, "utf-8"));
				out.write(bytes, 0, len);
			}
			out.flush();
			out.close();
		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("in decoding\n");
			e1.printStackTrace();
		}

	}
}
