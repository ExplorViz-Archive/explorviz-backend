package net.explorviz.history.server.resources;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.history.repository.persistence.ReplayRepository;
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
  public TimestampResource(final LandscapeRepository<Landscape> landscapeRepo,
      final ReplayRepository<Landscape> replayRepo) {
    this.landscapeRepo = landscapeRepo;
    this.replayRepo = replayRepo;
  }

  /**
   * Returns a list of either user-uploaded or service-generated
   * {@link net.explorviz.landscape.model.store.Timestamp}. The result depends on the passed query
   * parameters, whereas the existence of the "returnUploadedTimestamps" query parameter has the
   * highest priority, i.e., the list of user-uploaded timestamps will be returned.
   *
   * @param afterTimestamp - a starting timestamp for the returned interval
   * @param intervalSize - the size of the interval
   * @param returnUploadedTimestamps - switch between user-uploaded and service-generated timestamps
   * @return a filtered list of timestamps
   */
  @GET
  @Produces("application/vnd.api+json")
  public List<Timestamp> getTimestamps(@QueryParam("afterTimestamp") final long afterTimestamp,
      @QueryParam("intervalSize") final int intervalSize,
      @QueryParam("returnUploadedTimestamps") final boolean returnUploadedTimestamps) {

    List<Timestamp> timestamps = this.landscapeRepo.getAllTimestamps();

    if (returnUploadedTimestamps) {
      timestamps = this.replayRepo.getAllTimestamps();
    }

    return this.filterTimestampsAfterTimestamp(timestamps, afterTimestamp, intervalSize);

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

    // search the passed timestamp
    final Optional<Timestamp> potentialStartTimestamp =
        getTimestampPosition(allTimestamps, afterTimestamp);

    if (!potentialStartTimestamp.isPresent()) {
      return new LinkedList<>();
    }

    final int potentialStartingPosition = allTimestamps.indexOf(potentialStartTimestamp.get());

    // no timestamp was found
    if (potentialStartingPosition == -1) {
      return new LinkedList<>();
    } else {
      try {
        final int totalTimestampListSize = allTimestamps.size();

        if (intervalSize == 0 || intervalSize > totalTimestampListSize
            || potentialStartingPosition + intervalSize > totalTimestampListSize) {
          // return all timestamps starting at desired position
          return allTimestamps.subList(potentialStartingPosition, totalTimestampListSize);
        } else {
          // return desired interval of timestamps
          return allTimestamps.subList(potentialStartingPosition,
              potentialStartingPosition + intervalSize);
        }

      } catch (final IllegalArgumentException e) {
        throw new WebApplicationException(e);
      }
    }
  }

  /**
   * Retrieves the passed {@link Timestamp} within a list of timestamps if found, otherwise the
   * following timestamp.
   *
   * @param timestamps - a list of timestamps
   * @param searchedTimestamp - a specific timestamp to be found
   * @return an Optional containing the retrieved timestamp or emptys
   */
  private Optional<Timestamp> getTimestampPosition(final List<Timestamp> timestamps,
      final long searchedTimestamp) {

    final Iterator<Timestamp> iterator = timestamps.iterator();

    while (iterator.hasNext()) {

      final Timestamp currentTimestamp = iterator.next();

      // searched timestamp found
      if (currentTimestamp.getTimestamp() == searchedTimestamp) {
        return Optional.of(currentTimestamp);

        // next timestamp later than searched timestamp found
      } else if (currentTimestamp.getTimestamp() > searchedTimestamp) {
        return Optional.of(currentTimestamp);
      }
    }
    return Optional.empty();
  }

}
