package net.explorviz.landscape.server.resources;

import java.io.FileNotFoundException;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.sse.Sse;
import net.explorviz.landscape.api.ExtensionApiImpl;
import net.explorviz.landscape.server.main.Configuration;
import net.explorviz.shared.landscape.model.landscape.Landscape;

/**
 * Resource providing {@link Landscape} data for the frontend.
 */
@Path("v1/landscapes")
@RolesAllowed({"admin"})
public class LandscapeResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private final ExtensionApiImpl api;

  @Inject
  public LandscapeResource(final ExtensionApiImpl api) {
    this.api = api;
  }

  @GET
  @Path("/by-timestamp")
  @Produces(MEDIA_TYPE)
  public Landscape getLandscapeByTimestamp(@QueryParam("timestamp") final long timestamp) {
    return this.api.getLandscape(timestamp, Configuration.LANDSCAPE_REPOSITORY);
  }

  @Path("/broadcast")
  public LandscapeBroadcastSubResource getLandscapeBroadcastResource(@Context final Sse sse,
      @Context final LandscapeBroadcastSubResource landscapeBroadcastSubResource) {

    // curl -v -X GET http://localhost:8081/v1/landscapes/broadcast/ -H
    // "Content-Type: text/event-stream" -H 'Authorization: Bearer <token>'

    return landscapeBroadcastSubResource;
  }

  @GET
  @Path("/by-uploaded-timestamp/{timestamp}")
  @Produces(MEDIA_TYPE)
  public Landscape getReplayLandscape(@PathParam("timestamp") final long timestamp)
      throws FileNotFoundException {
    return this.api.getLandscape(timestamp, Configuration.REPLAY_REPOSITORY);
  }

}
