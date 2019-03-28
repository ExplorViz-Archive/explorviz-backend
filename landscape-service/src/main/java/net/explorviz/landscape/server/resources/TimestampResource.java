package net.explorviz.landscape.server.resources;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import net.explorviz.landscape.api.ExtensionApiImpl;
import net.explorviz.landscape.repository.persistence.LandscapeRepository;
import net.explorviz.landscape.repository.persistence.ReplayRepository;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.landscape.model.store.Timestamp;

/**
 * REST resource providing {@link net.explorviz.landscape.model.store.Timestamp} data for the
 * frontend.
 */
@Path("v1/timestamps")
@RolesAllowed({"admin"})
public class TimestampResource {

  private final LandscapeRepository<Landscape> landscapeRepo;
  private final ReplayRepository<Landscape> replayRepo;

  @Inject
  public TimestampResource(final ExtensionApiImpl api,
      final LandscapeRepository<Landscape> landscapeRepo,
      final ReplayRepository<Landscape> replayRepo) {
    this.landscapeRepo = landscapeRepo;
    this.replayRepo = replayRepo;
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
    final List<Timestamp> timestamps = this.landscapeRepo.getAllTimestamps();

    return this.filterTimestampsAfterTimestamp(timestamps, afterTimestamp, intervalSize);
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
    return this.replayRepo.getAllTimestamps();
  }


  /**
   * Retrieves timestamps AFTER a passed
   * {@link net.explorviz.shared.landscape.model.store.Timestamp}.
   *
   * @param allTimestamps - All timestamps within the system
   * @param afterTimestamp - Define the timestamp which sets the limit
   * @param intervalSize - The number of retrieved timestamps
   * @return List of Timestamp
   */
  private List<Timestamp> filterTimestampsAfterTimestamp(final List<Timestamp> allTimestamps,
      final long afterTimestamp, final int intervalSize) {

    final int timestampListSize = allTimestamps.size();

    // search the passed timestamp
    final Timestamp foundTimestamp = getTimestampPosition(allTimestamps, afterTimestamp);
    final int foundTimestampPosition = allTimestamps.indexOf(foundTimestamp);

    // no timestamp was found
    if (foundTimestampPosition == -1) {
      return new LinkedList<>();
    } else {
      try {
        if (intervalSize == 0 || intervalSize > timestampListSize) {
          // all timestamps starting at position
          return allTimestamps.subList(foundTimestampPosition, timestampListSize);
        } else {
          if (foundTimestampPosition + intervalSize > timestampListSize) {
            return allTimestamps.subList(foundTimestampPosition, timestampListSize);
          } else {
            return allTimestamps.subList(foundTimestampPosition,
                foundTimestampPosition + intervalSize);
          }
        }
      } catch (final IllegalArgumentException e) {
        throw new WebApplicationException(e);
      }
    }
  }

  /**
   * Retrieves the a passed {@link Timestamp} within a list of timestamps if found, otherwise the
   * following timestamp.
   *
   * @param timestamps - a list of timestamps
   * @param searchedTimestamp - a specific timestamp to be found
   * @return a retrieved timestamp
   */
  private static Timestamp getTimestampPosition(final List<Timestamp> timestamps,
      final long searchedTimestamp) {

    final Iterator<Timestamp> iterator = timestamps.iterator();

    while (iterator.hasNext()) {

      final Timestamp currentTimestamp = iterator.next();

      // searched timestamp found
      if (currentTimestamp.getTimestamp() == searchedTimestamp) {
        return currentTimestamp;
      }
      // next timestamp later than searched timestamp found
      else if (currentTimestamp.getTimestamp() > searchedTimestamp) {
        return currentTimestamp;
      }
    }
    return new Timestamp();
  }

}
