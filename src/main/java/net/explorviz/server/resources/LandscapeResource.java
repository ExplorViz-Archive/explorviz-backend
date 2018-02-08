package net.explorviz.server.resources;

import java.io.FileNotFoundException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import net.explorviz.api.ExtensionAPI;
import net.explorviz.api.ExtensionAPIImpl;
import net.explorviz.model.Landscape;
import net.explorviz.server.security.Secured;

/**
 * REST resource providing landscape data for the frontend
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 */
@Secured
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
}
