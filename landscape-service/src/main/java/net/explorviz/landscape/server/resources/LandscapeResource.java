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
import net.explorviz.landscape.repository.persistence.mongo.MongoLandscapeJsonApiRepository;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * Resource providing {@link Landscape} data for the frontend.
 */
@Path("v1/landscapes")
@RolesAllowed({"admin"})
public class LandscapeResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeResource.class);
  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private final ExtensionApiImpl api;

  private final Jedis jedis;

  private final MongoLandscapeJsonApiRepository landscapeJsonApiRepo;



  @Inject
  public LandscapeResource(final ExtensionApiImpl api, final Jedis jedis,
      final MongoLandscapeJsonApiRepository landscapeJsonApiRepo) {
    this.api = api;
    this.jedis = jedis;
    this.landscapeJsonApiRepo = landscapeJsonApiRepo;
  }

  @GET
  @Path("/by-timestamp")
  @Produces(MEDIA_TYPE)
  public String getLandscapeByTimestamp(@QueryParam("timestamp") final long timestamp) {
    final String potentialCachedLandscape = this.jedis.get(String.valueOf(timestamp));

    if (potentialCachedLandscape == null) {
      LOGGER.info("Used mongodb");
      return this.landscapeJsonApiRepo.getByTimestamp(timestamp);
    } else {
      LOGGER.info("Used cache");
      return potentialCachedLandscape;
    }


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
    return this.api.getLandscape(timestamp);
  }

}
