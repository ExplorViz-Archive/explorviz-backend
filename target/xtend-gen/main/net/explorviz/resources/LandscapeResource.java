package net.explorviz.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.explorviz.model.Landscape;
import net.explorviz.server.repository.LandscapeRepositoryModel;

@Path("currentLandscape")
@SuppressWarnings("all")
public class LandscapeResource {
  private LandscapeRepositoryModel service;
  
  @Inject
  public LandscapeRepositoryModel LandscapeResource(final LandscapeRepositoryModel service) {
    return this.service = service;
  }
  
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  @Path("/landscape")
  public Landscape getLandscape() {
    return this.service.getLastPeriodLandscape();
  }
}
