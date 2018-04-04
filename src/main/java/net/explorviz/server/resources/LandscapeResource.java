package net.explorviz.server.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
		return api.getLandscape(timestamp);
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
		// TODO encode with base64
		final File landscapeRepository = new File(
				FileSystemHelper.getExplorVizDirectory() + "/" + "landscapeRepository/");
		// https://stackoverflow.com/questions/13515150/how-to-get-file-from-directory-with-pattern-filter
		final File[] filesWithTimestamp = landscapeRepository.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return name.matches("^" + timestamp + "\\-[0-9]\\.expl");
			}
		});
		final File exportLandscape = new File(filesWithTimestamp[0].getAbsolutePath());

		return Response.ok(exportLandscape, "*/*").build();
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/by-uploaded-timestamp/{timestamp}")
	public Landscape getUploadedLandscape(@PathParam("timestamp") final long timestamp) throws FileNotFoundException {
		return api.getUploadedLandscape(timestamp);
	}

	// https://stackoverflow.com/questions/25797650/fileupload-with-jax-rs
	@Consumes("multipart/form-data")
	@POST
	@Path("/upload-landscape")
	public Response uploadLandscape(@FormDataParam("file") final InputStream uploadedInputStream,
			@FormDataParam("file") final FormDataContentDisposition fileInfo) {

		final String uploadedLandscapeFilePath = FileSystemHelper.getExplorVizDirectory() + "/";
		System.out.println("uploadedLandscapeRepository: " + uploadedLandscapeFilePath);

		new File(uploadedLandscapeFilePath + "uploadedLandscapeRepository/").mkdir();
		final File objFile = new File(
				uploadedLandscapeFilePath + "uploadedLandscapeRepository/" + fileInfo.getFileName());
		if (objFile.exists()) {
			objFile.delete();

		}

		saveToFile(uploadedInputStream,
				uploadedLandscapeFilePath + "uploadedLandscapeRepository/" + fileInfo.getFileName());

		// TODO send other response, if sth. went wrong
		return Response.ok().build();
	}

	private void saveToFile(final InputStream uploadedInputStream, final String uploadedFileLocation) {

		try {
			OutputStream out = null;
			int read = 0;
			final byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (final IOException e) {

			e.printStackTrace();
		}

	}
}
