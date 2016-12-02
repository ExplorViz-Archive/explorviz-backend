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
import com.github.jasminb.jsonapi.SerializationFeature
import com.github.jasminb.jsonapi.JSONAPIDocument
import net.explorviz.layout.LayoutService

@Path("landscapes")
class LandscapeResource {

	var LandscapeRepositoryModel service

	@Inject
	def LandscapeResource(LandscapeRepositoryModel service) {
		this.service = service
	}

	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/{landscapeId}")
	def byte[] getLandscape(@PathParam("landscapeId") String landscapeId) {

		var landscape = LayoutService.layoutLandscape(service.lastPeriodLandscape)	
		
		// IDs need to be generated in some way
		//landscape.id = "1"	
		
		var ResourceConverter converter = new ResourceConverter(Landscape, net.explorviz.model.System)
		converter.enableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES)

		var JSONAPIDocument<Landscape> document = new JSONAPIDocument<Landscape>(landscape)

		converter.writeDocument(document)
		
	}
}
