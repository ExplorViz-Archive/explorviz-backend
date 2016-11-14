package net.explorviz.resources

import javax.inject.Inject
import javax.ws.rs.core.MediaType
import javax.ws.rs.Produces
import javax.ws.rs.GET
import javax.ws.rs.Path
import net.explorviz.server.repository.LandscapeRepositoryModel
import net.explorviz.model.Landscape
import net.explorviz.layout.LayoutService
import javax.annotation.security.PermitAll

@Path("currentLandscape")
@PermitAll
class LandscapeResource {

	var LandscapeRepositoryModel service

	@Inject
	def LandscapeResource(LandscapeRepositoryModel service) {
		this.service = service
	}

	//@RolesAllowed("admin")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/landscape")
	def Landscape getLandscape() {
		LayoutService::layoutLandscape(this.service.getLastPeriodLandscape())		
	}
}
