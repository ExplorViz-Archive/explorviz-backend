package net.explorviz.server.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import net.explorviz.api.ExtensionAPI;
import net.explorviz.api.ExtensionAPIImpl;
import net.explorviz.model.Timestamp;
import net.explorviz.server.security.Secured;

/**
 * REST resource providing timestamp data for the frontend
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 */
@Secured
@Path("timestamp")
public class TimestampResource {

	private final ExtensionAPIImpl api = ExtensionAPI.get();

	@Produces("application/vnd.api+json")
	@GET
	@Path("/after-timestamp/{timestamp}")
	public List<Timestamp> getSubsequentTimestamps(@PathParam("timestamp") final long afterTimestamp,
			@QueryParam("intervalSize") final int intervalSize) {

		return api.getSubsequentTimestamps(afterTimestamp, intervalSize);
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/before-timestamp/{timestamp}")
	public List<Timestamp> getPreviousTimestamps(@PathParam("timestamp") final long beforeTimestamp,
			@QueryParam("intervalSize") final int intervalSize) {

		return api.getPreviousTimestamps(beforeTimestamp, intervalSize);
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/from-oldest")
	public List<Timestamp> getOldestTimestamps(@QueryParam("intervalSize") final int intervalSize) {

		return api.getOldestTimestamps(intervalSize);
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/from-recent")
	public List<Timestamp> getNewestTimestamps(@QueryParam("intervalSize") final int intervalSize) {

		return api.getNewestTimestamps(intervalSize);
	}

}
