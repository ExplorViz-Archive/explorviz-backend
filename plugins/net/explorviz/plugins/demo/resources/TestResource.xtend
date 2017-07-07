package net.explorviz.plugins.demo.resources

import javax.inject.Inject
import javax.ws.rs.core.MediaType
import javax.ws.rs.Produces
import javax.ws.rs.GET
import javax.ws.rs.Path
import net.explorviz.server.repository.LandscapeRepositoryModel
import net.explorviz.server.security.Secured
import com.github.jasminb.jsonapi.ResourceConverter
import net.explorviz.model.Landscape
import com.github.jasminb.jsonapi.JSONAPIDocument
import net.explorviz.server.repository.LandscapeExchangeService

@Secured
@Path("plugin/test")
class TestResource {

	var LandscapeRepositoryModel model
	var ResourceConverter converter
	var LandscapeExchangeService service

	@Inject
	def LandscapeResource(LandscapeRepositoryModel model, ResourceConverter converter,
		LandscapeExchangeService service) {
		this.model = model
		this.converter = converter
		this.service = service
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
