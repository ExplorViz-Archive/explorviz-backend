package net.explorviz.server.resources;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import net.explorviz.api.ExtensionAPIImpl;
import net.explorviz.model.store.Timestamp;

/**
 * REST resource providing timestamp data for the frontend.
 */
// @Secured
@Path("v1/timestamps")
public class TimestampResource {

  private final ExtensionAPIImpl api;

  @Inject
  public TimestampResource(final ExtensionAPIImpl api) {
    this.api = api;
  }

  @GET
  @Produces("application/vnd.api+json")
  public List<Timestamp> getSubsequentTimestamps(@QueryParam("after") final long afterTimestamp,
      @QueryParam("before") final long beforeTimestamp,
      @QueryParam("intervalSize") final int intervalSize) {

    // return api.getPreviousTimestamps(beforeTimestamp, intervalSize);
    return api.getSubsequentTimestamps(afterTimestamp, intervalSize);
  }

  /*
   * @GET
   *
   * @Path("/from-oldest")
   *
   * @Produces("application/vnd.api+json") public List<Timestamp>
   * getOldestTimestamps(@QueryParam("intervalSize") final int intervalSize) {
   *
   * return api.getOldestTimestamps(intervalSize); }
   *
   * @GET
   *
   * @Path("/from-recent")
   *
   * @Produces("application/vnd.api+json")
   *
   * public List<Timestamp> getNewestTimestamps(@QueryParam("intervalSize") final int intervalSize)
   * {
   *
   * return api.getNewestTimestamps(intervalSize); }
   *
   * @GET
   *
   * @Path("/all-uploaded")
   *
   * @Produces("application/vnd.api+json") public List<Timestamp> getUploadedTimestamps() { return
   * api.getUploadedTimestamps(); }
   */

}
