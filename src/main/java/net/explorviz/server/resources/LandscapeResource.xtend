package net.explorviz.server.resources

import javax.inject.Inject
import javax.ws.rs.Produces
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import net.explorviz.server.security.Secured
import net.explorviz.model.Landscape
import java.io.FileNotFoundException
import net.explorviz.repository.LandscapeExchangeService

@Secured
@Path("landscape")
class LandscapeResource {

	var LandscapeExchangeService service

	@Inject
	new(LandscapeExchangeService service) {
		this.service = service
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/by-timestamp/{timestamp}")
	def Landscape getLandscape(@PathParam("timestamp") long timestamp) throws FileNotFoundException {
		service.getLandscape(timestamp)
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/latest-landscape")
	def Landscape getLatestLandscape() {
		service.currentLandscape
	}
}
