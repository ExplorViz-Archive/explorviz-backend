package net.explorviz.history.server.resources;

import java.io.FileNotFoundException;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.history.repository.persistence.ReplayRepository;
import net.explorviz.shared.landscape.model.landscape.Landscape;


/**
 * Resource providing persisted {@link Landscape} data for the frontend.
 */
@Path("v1/landscapes")
@RolesAllowed({"admin"})
public class LandscapeResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private final LandscapeRepository<String> landscapeRepo;
  private final ReplayRepository<String> replayRepo;


  @Inject
  public LandscapeResource(final LandscapeRepository<String> landscapeRepo,
      final ReplayRepository<String> replayRepo) {
    this.landscapeRepo = landscapeRepo;
    this.replayRepo = replayRepo;
  }

  @GET
  @Path("/by-timestamp")
  @Produces(MEDIA_TYPE)
  public String getLandscapeByTimestamp(@QueryParam("timestamp") final long timestamp) {
    return this.landscapeRepo.getByTimestamp(timestamp);
  }

  @GET
  @Path("/by-uploaded-timestamp/{timestamp}")
  @Produces(MEDIA_TYPE)
  public String getReplayLandscape(@PathParam("timestamp") final long timestamp)
      throws FileNotFoundException {
    return this.replayRepo.getByTimestamp(timestamp);
  }

}
