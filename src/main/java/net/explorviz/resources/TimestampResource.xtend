package net.explorviz.resources

import javax.inject.Inject
import javax.ws.rs.core.MediaType
import javax.ws.rs.Produces
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import net.explorviz.server.security.Secured
import net.explorviz.server.repository.LandscapeExchangeService
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.ObjectMapper

@Path("timestamp")
class TimestampResource {

	var LandscapeExchangeService service

	@Inject
	def LandscapeResource(LandscapeExchangeService service) {
		this.service = service
	}
	
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
		
		var listToJson = objectMapper.writeValueAsString(timestamps)
	
		data.put("attributes", listToJson)
		
		objectMapper.writeValueAsBytes(response)
	}

}
