package net.explorviz.resources

import javax.inject.Inject
import javax.ws.rs.core.MediaType
import javax.ws.rs.Produces
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import net.explorviz.server.security.Secured
import net.explorviz.model.Landscape
import net.explorviz.server.repository.LandscapeExchangeService
import java.io.FileNotFoundException

@Secured
@Path("landscape")
class LandscapeResource {

	var LandscapeExchangeService service

	@Inject
	new(LandscapeExchangeService service) {
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
		service.currentLandscape
	}
}
