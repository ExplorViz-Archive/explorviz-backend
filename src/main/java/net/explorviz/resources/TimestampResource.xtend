package net.explorviz.resources

import javax.inject.Inject
import javax.ws.rs.core.MediaType
import javax.ws.rs.Produces
import javax.ws.rs.GET
import javax.ws.rs.Path
import net.explorviz.server.security.Secured
import net.explorviz.server.repository.LandscapeExchangeService
import com.github.jasminb.jsonapi.JSONAPIDocument
import com.github.jasminb.jsonapi.ResourceConverter
import javax.ws.rs.QueryParam
import javax.ws.rs.PathParam
import java.util.List
import net.explorviz.model.Timestamp

@Secured
@Path("timestamp")
class TimestampResource {

	var LandscapeExchangeService service
	var ResourceConverter converter

	@Inject
	def LandscapeResource(ResourceConverter converter, LandscapeExchangeService service) {
		this.converter = converter
		this.service = service
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/after-timestamp/{timestamp}")
	def byte[] getSubsequentTimestamps(@PathParam("timestamp") long timestamp,
		@QueryParam("intervalSize") int intervalSize) {

		var timestampStorage = this.service.timestampObjectsInRepo

		var filteredTimestamps = timestampStorage.filterTimestampsAfterTimestamp(timestamp, intervalSize)

		var JSONAPIDocument<List<Timestamp>> document = new JSONAPIDocument<List<Timestamp>>(filteredTimestamps)
		this.converter.writeDocumentCollection(document)
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/before-timestamp/{timestamp}")
	def byte[] getPreviousTimestamps(@PathParam("timestamp") long timestamp,
		@QueryParam("intervalSize") int intervalSize) {

		var timestampStorage = this.service.timestampObjectsInRepo

		var filteredTimestamps = timestampStorage.filterTimestampsBeforeTimestamp(timestamp, intervalSize)

		var JSONAPIDocument<List<Timestamp>> document = new JSONAPIDocument<List<Timestamp>>(filteredTimestamps)
		this.converter.writeDocumentCollection(document)
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/from-oldest")
	def byte[] getOldestTimestamps(@QueryParam("intervalSize") int intervalSize) {

		var timestampStorage = this.service.timestampObjectsInRepo

		var filteredTimestamps = timestampStorage.filterOldestTimestamps(intervalSize)

		var JSONAPIDocument<List<Timestamp>> document = new JSONAPIDocument<List<Timestamp>>(filteredTimestamps)
		this.converter.writeDocumentCollection(document)
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/from-recent")
	def byte[] getNewestTimestamps(@QueryParam("intervalSize") int intervalSize) {

		var timestampStorage = this.service.timestampObjectsInRepo

		var filteredTimestamps = timestampStorage.filterMostRecentTimestamps(intervalSize)


		var JSONAPIDocument<List<Timestamp>> document = new JSONAPIDocument<List<Timestamp>>(filteredTimestamps)
		this.converter.writeDocumentCollection(document)
	}

}
