package net.explorviz.resources

import javax.inject.Inject
import javax.ws.rs.core.MediaType
import javax.ws.rs.Produces
import javax.ws.rs.GET
import javax.ws.rs.Path
import net.explorviz.server.repository.LandscapeRepositoryModel
import javax.ws.rs.PathParam
import net.explorviz.server.security.Secured
import com.github.jasminb.jsonapi.ResourceConverter
import net.explorviz.model.Landscape
import com.github.jasminb.jsonapi.JSONAPIDocument
import net.explorviz.server.repository.LandscapeExchangeService
import java.io.FileNotFoundException
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.ObjectMapper

@Secured
@Path("landscape")
class LandscapeResource {

	var LandscapeRepositoryModel model
	var ResourceConverter converter
	var LandscapeExchangeService service

	@Inject
	new (LandscapeRepositoryModel model, ResourceConverter converter,
		LandscapeExchangeService service) {
		this.model = model
		this.converter = converter
		this.service = service
	}

	
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/by-timestamp/{timestamp}")
	def byte[] getLandscape(@PathParam("timestamp") long timestamp) {

		var Landscape landscape

		try {
			landscape = service.getLandscape(timestamp)
		} catch (FileNotFoundException e) {
			var factory = JsonNodeFactory.instance

			var response = factory.objectNode
			var error = factory.objectNode

			response.set("error", error)

			error.put("type", "NoSuchFileFound")
			error.put("detail", "Your request contains a non-valid or unknown landscape timestamp.")
		
			var objectMapper = new ObjectMapper
			
			objectMapper.writeValueAsBytes(response)
		}

		var JSONAPIDocument<Landscape> document = new JSONAPIDocument<Landscape>(landscape)

		this.converter.writeDocument(document)
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/latest-landscape")
	def byte[] getLatestLandscape() {

		var landscape = model.lastPeriodLandscape
		
		var JSONAPIDocument<Landscape> document = new JSONAPIDocument<Landscape>(landscape)

		this.converter.writeDocument(document)
	}
}
