package net.explorviz.landscape.server.resources;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import net.explorviz.landscape.api.ExtensionApiImpl;
import net.explorviz.shared.landscape.model.store.Timestamp;

/**
 * REST resource providing {@link net.explorviz.landscape.model.store.Timestamp} data for the
 * frontend.
 */
@Path("v1/timestamps")
@RolesAllowed({"admin"})
public class TimestampResource {

  private final ExtensionApiImpl api;

  @Inject
  public TimestampResource(final ExtensionApiImpl api) {
    this.api = api;
  }

  /**
   * Returns an List of {@link net.explorviz.landscape.model.store.Timestamp} interval of
   * "intervalSize" after a specific passed "timestamp"
   *
   * @param afterTimestamp - a starting timestamp for the returned interval
   * @param intervalSize - the size of the interval
   * @return a filtered list of timestamps
   */
  @GET
  @Path("/subsequent-interval")
  @Produces("application/vnd.api+json")
  public List<Timestamp> getSubsequentTimestamps(@QueryParam("after") final long afterTimestamp,
      @QueryParam("intervalSize") final int intervalSize) {
    return this.api.getSubsequentTimestamps(afterTimestamp, intervalSize);
  }

  /**
   * Returns a List of all uploaded {@link net.explorviz.landscape.model.store.Timestamp}
   *
   * @return a list of all uploaded timestamps
   */
  @GET
  @Path("/all-uploaded")
  @Produces("application/vnd.api+json")
  public List<Timestamp> getUploadedTimestamps() {
    return this.api.getUploadedTimestamps();
  }

}
