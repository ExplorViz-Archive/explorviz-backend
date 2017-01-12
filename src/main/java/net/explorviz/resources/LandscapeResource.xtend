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
import net.explorviz.server.repository.LandscapeExchangeService
import java.io.FileNotFoundException

@Path("landscape")
class LandscapeResource {

	var LandscapeRepositoryModel model
	var ResourceConverter converter
	var LandscapeExchangeService service

	@Inject
	def LandscapeResource(LandscapeRepositoryModel model, ResourceConverter converter, LandscapeExchangeService service) {
		this.model = model
		this.converter = converter
		this.service = service
	}

	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/by-timestamp/{timestamp}")
	def byte[] getLandscape(@PathParam("timestamp") long timestamp) {	
		
		var Landscape landscape

		try{
			landscape = LayoutService.layoutLandscape(service.getLandscape(timestamp))
		} catch(FileNotFoundException e) {
			// return (as Json object, that no file was found)
		}		
		
		var JSONAPIDocument<Landscape> document = new JSONAPIDocument<Landscape>(landscape)

		this.converter.writeDocument(document)
	}
	
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/latest-landscape")
	def byte[] getLatestLandscape() {		

		//var landscape = LayoutService.layoutLandscape(model.lastPeriodLandscape)
		var landscape = LayoutService.layoutLandscape(LandscapeDummyCreator::createDummyLandscape)
		
		var JSONAPIDocument<Landscape> document = new JSONAPIDocument<Landscape>(landscape)

		this.converter.writeDocument(document)
	}
}
