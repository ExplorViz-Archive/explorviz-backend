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
import net.explorviz.model.Landscape

@Path("timestamp")
class TimestampResource {

	var LandscapeExchangeService service
	var ResourceConverter converter

	@Inject
	def LandscapeResource(ResourceConverter converter, LandscapeExchangeService service) {
		this.converter = converter
		this.service = service
	}
	
	/* 
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/show-timestamps")
	def byte[] getTimestamps(@PathParam("timestamp") long timestamp) {
		var timestamps = this.service.namesInRepo
		
		var factory = JsonNodeFactory.instance
		
		var objectMapper = new ObjectMapper

		var response = factory.objectNode
		var data = factory.objectNode
	
		response.set("data", data)
	
		data.put("type", "timestamp-array")
		data.put("id", "1")
		
		var timestampsJson = objectMapper.writeValueAsString(timestamps)
		
		var attributes = factory.objectNode
	
		data.set("attributes", attributes)
		
		attributes.put("timestamps", timestampsJson)
		
		objectMapper.writeValueAsBytes(response)
	}
	*/
	
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/show-timestamps")
	def byte[] getTimestamps() {	
		var timestampStorage = new TimestampStorage("0")
		timestampStorage = this.service.timestampObjectsInRepo
		
		var JSONAPIDocument<TimestampStorage> document = new JSONAPIDocument<TimestampStorage>(timestampStorage)
		this.converter.writeDocument(document)
	}

}
