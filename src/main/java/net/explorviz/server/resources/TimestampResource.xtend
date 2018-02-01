package net.explorviz.server.resources

import javax.inject.Inject
import javax.ws.rs.Produces
import javax.ws.rs.GET
import javax.ws.rs.Path
import net.explorviz.server.security.Secured
import javax.ws.rs.QueryParam
import javax.ws.rs.PathParam
import java.util.List
import net.explorviz.model.Timestamp
import net.explorviz.model.helper.TimestampHelper
import net.explorviz.repository.LandscapeExchangeService

@Secured
@Path("timestamp")
class TimestampResource {

	var LandscapeExchangeService service

	@Inject
	new(LandscapeExchangeService service) {
		this.service = service
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/after-timestamp/{timestamp}")
	def List<Timestamp> getSubsequentTimestamps(@PathParam("timestamp") long timestamp,
		@QueryParam("intervalSize") int intervalSize) {

		var allTimestamps = this.service.timestampObjectsInRepo
		var filteredTimestamps = TimestampHelper.filterTimestampsAfterTimestamp(allTimestamps, timestamp, intervalSize)
		return filteredTimestamps
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/before-timestamp/{timestamp}")
	def List<Timestamp> getPreviousTimestamps(@PathParam("timestamp") long timestamp,
		@QueryParam("intervalSize") int intervalSize) {

		var allTimestamps = this.service.timestampObjectsInRepo
		var filteredTimestamps = TimestampHelper.filterTimestampsBeforeTimestamp(allTimestamps, timestamp, intervalSize)
		return filteredTimestamps
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/from-oldest")
	def List<Timestamp> getOldestTimestamps(@QueryParam("intervalSize") int intervalSize) {

		var allTimestamps = this.service.timestampObjectsInRepo
		var filteredTimestamps = TimestampHelper.filterOldestTimestamps(allTimestamps, intervalSize)
		return filteredTimestamps
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/from-recent")
	def List<Timestamp> getNewestTimestamps(@QueryParam("intervalSize") int intervalSize) {

		var allTimestamps = this.service.timestampObjectsInRepo
		var filteredTimestamps = TimestampHelper.filterMostRecentTimestamps(allTimestamps, intervalSize)
		return filteredTimestamps
	}

}