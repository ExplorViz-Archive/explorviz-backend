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
import net.explorviz.server.repository.LandscapeExchangeService
import java.io.FileNotFoundException

@Secured
@Path("landscape")
class LandscapeResource {

	var LandscapeRepositoryModel model
	var ResourceConverter converter
	var LandscapeExchangeService service

	@Inject
	new(LandscapeRepositoryModel model, ResourceConverter converter, LandscapeExchangeService service) {
		this.model = model
		this.converter = converter
		this.service = service
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/by-timestamp/{timestamp}")
	def Landscape getLandscape(@PathParam("timestamp") long timestamp) throws FileNotFoundException {
		service.getLandscape(timestamp)
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/latest-landscape")
	def Landscape getLatestLandscape() {
		model.lastPeriodLandscape
	}
}
