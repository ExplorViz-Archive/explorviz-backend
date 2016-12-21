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
import net.explorviz.layout.LayoutService
import net.explorviz.server.repository.LandscapeDummyCreator

@Path("landscapes")
class LandscapeResource {

	var LandscapeRepositoryModel service
	var ResourceConverter converter

	@Inject
	def LandscapeResource(LandscapeRepositoryModel service, ResourceConverter converter) {
		this.service = service
		this.converter = converter
	}

	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/{landscapeId}")
	def byte[] getLandscape(@PathParam("landscapeId") String landscapeId) {		

		//var landscape = LayoutService.layoutLandscape(service.lastPeriodLandscape)
		var landscape = LayoutService.layoutLandscape(LandscapeDummyCreator::createDummyLandscape)
		
		var JSONAPIDocument<Landscape> document = new JSONAPIDocument<Landscape>(landscape)

		this.converter.writeDocument(document)

	}
}
