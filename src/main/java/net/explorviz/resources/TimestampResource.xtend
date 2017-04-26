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
import net.explorviz.model.TimestampStorage
import javax.ws.rs.QueryParam
import javax.ws.rs.PathParam

//@Secured
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

		var timestampStorage = new TimestampStorage("0")
		timestampStorage = this.service.timestampObjectsInRepo

		var filteredTimestamps = timestampStorage.filterTimestampsAfterTimestamp(timestamp, intervalSize)

		val filteredTimestampStorage = new TimestampStorage("0")
		filteredTimestampStorage.timestamps = filteredTimestamps

		var JSONAPIDocument<TimestampStorage> document = new JSONAPIDocument<TimestampStorage>(filteredTimestampStorage)
		this.converter.writeDocument(document)
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/before-timestamp/{timestamp}")
	def byte[] getPreviousTimestamps(@PathParam("timestamp") long timestamp,
		@QueryParam("intervalSize") int intervalSize) {

		var timestampStorage = new TimestampStorage("0")
		timestampStorage = this.service.timestampObjectsInRepo

		var filteredTimestamps = timestampStorage.filterTimestampsBeforeTimestamp(timestamp, intervalSize)

		val filteredTimestampStorage = new TimestampStorage("0")
		filteredTimestampStorage.timestamps = filteredTimestamps

		var JSONAPIDocument<TimestampStorage> document = new JSONAPIDocument<TimestampStorage>(filteredTimestampStorage)
		this.converter.writeDocument(document)
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/from-oldest")
	def byte[] getOldestTimestamps(@QueryParam("intervalSize") int intervalSize) {

		var timestampStorage = new TimestampStorage("0")
		timestampStorage = this.service.timestampObjectsInRepo

		var filteredTimestamps = timestampStorage.filterOldestTimestamps(intervalSize)

		val filteredTimestampStorage = new TimestampStorage("0")
		filteredTimestampStorage.timestamps = filteredTimestamps

		var JSONAPIDocument<TimestampStorage> document = new JSONAPIDocument<TimestampStorage>(filteredTimestampStorage)
		this.converter.writeDocument(document)
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/from-recent")
	def byte[] getNewestTimestamps(@QueryParam("intervalSize") int intervalSize) {

		var timestampStorage = new TimestampStorage("0")
		timestampStorage = this.service.timestampObjectsInRepo

		var filteredTimestamps = timestampStorage.filterMostRecentTimestamps(intervalSize)

		val filteredTimestampStorage = new TimestampStorage("0")
		filteredTimestampStorage.timestamps = filteredTimestamps

		var JSONAPIDocument<TimestampStorage> document = new JSONAPIDocument<TimestampStorage>(filteredTimestampStorage)
		this.converter.writeDocument(document)
	}

}
