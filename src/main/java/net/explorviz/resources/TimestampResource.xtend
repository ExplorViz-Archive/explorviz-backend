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
import net.explorviz.model.Timestamp
import java.util.List

@Path("timestamp")
class TimestampResource {

	var LandscapeExchangeService service
	var ResourceConverter converter

	@Inject
	def LandscapeResource(ResourceConverter converter, LandscapeExchangeService service) {
		this.converter = converter
		this.service = service
	}

	/**
	 * Returns all avaiable timestamps (landscapes) on the server
	 */
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/show-timestamps")
	def byte[] getTimestamps(@QueryParam("fromOldest") boolean fromOldest, @QueryParam("fromTimestamp") long fromTimestamp,
		@QueryParam("intervalSize") int intervalSize) {

		var timestampStorage = new TimestampStorage("0")
		timestampStorage = this.service.timestampObjectsInRepo

		var List<Timestamp> filteredTimestamps
		
		if(fromOldest) {
			filteredTimestamps = timestampStorage.filterTimestampsFromOldest(intervalSize)
		} 
		else {
			filteredTimestamps = timestampStorage.filterTimestampsFromTimestamp(fromTimestamp, intervalSize)
		}		
		
		val filteredTimestampStorage = new TimestampStorage("0")
		filteredTimestampStorage.timestamps = filteredTimestamps

		var JSONAPIDocument<TimestampStorage> document = new JSONAPIDocument<TimestampStorage>(filteredTimestampStorage)
		this.converter.writeDocument(document)
	}

}
