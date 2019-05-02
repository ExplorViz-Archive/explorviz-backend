package net.explorviz.history.server.resources;

import java.util.List;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
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

  private static final long DEFAULT_QUERY_VALUE_TIMESTAMP = 0L;

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
   * @param startTimestamp - a starting timestamp for the returned interval
   * @param intervalSize - the size of the interval
   * @param returnUploadedTimestamps - switch between user-uploaded and service-generated timestamps
   * @return a filtered list of timestamps
   */
  @GET
  @Produces("application/vnd.api+json")
  public List<Timestamp> getTimestamps(@QueryParam("startTimestamp") final long startTimestamp,
      @QueryParam("intervalSize") final int intervalSize,
      @QueryParam("returnUploadedTimestamps") final boolean returnUploadedTimestamps) {

    List<Timestamp> timestamps = this.landscapeRepo.getAllTimestamps();

    if (returnUploadedTimestamps) {
      timestamps = this.replayRepo.getAllTimestamps();
    }

    return this.getTimestampInterval(timestamps, startTimestamp, intervalSize);
  }


  /**
   * Get an interval (List) of {@link net.explorviz.shared.landscape.model.store.Timestamp} starting
   * at a passed timestamp value.
   *
   * @param allTimestamps - timestamp list
   * @param afterTimestamp - Starting point of interval
   * @param intervalSize - The number of retrieved timestamps, if 0 or bigger that (partial) list,
   *        then returns all following timestamps
   * @return List of Timestamp
   */
  private List<Timestamp> getTimestampInterval(final List<Timestamp> allTimestamps,
      final long afterTimestamp, final int intervalSize) {

    if (afterTimestamp == DEFAULT_QUERY_VALUE_TIMESTAMP) {
      return allTimestamps;
    }

    // search the passed timestamp
    final Optional<Timestamp> potentialStartTimestamp =
        this.getTimestampPosition(allTimestamps, afterTimestamp);

    if (!potentialStartTimestamp.isPresent()) {
      throw new NotFoundException("The passed timestamp value does not exist in the system.");
    }

    final int potentialStartingPosition = allTimestamps.indexOf(potentialStartTimestamp.get());

    final int totalTimestampListSize = allTimestamps.size();

    int updatedIntervalSize = intervalSize;

    // intervalSize shorten by one, since starting point does also count
    if (intervalSize >= 2) { // NOPMD
      updatedIntervalSize -= 1;
    }

    try {
      if (updatedIntervalSize == 0 || updatedIntervalSize > totalTimestampListSize
          || potentialStartingPosition + updatedIntervalSize > totalTimestampListSize) {
        // return all timestamps starting at desired position
        return allTimestamps.subList(potentialStartingPosition, totalTimestampListSize);
      } else {
        // return exact desired interval of timestamps
        return allTimestamps.subList(potentialStartingPosition,
            potentialStartingPosition + updatedIntervalSize);
      }
    } catch (final IllegalArgumentException e) {
      throw new WebApplicationException(
          "Your timestamp interval could not be processed. This shouldn't happen.", e);
    }

  }

  /**
   * Retrieves the passed {@link Timestamp} within a list of timestamps if found.
   *
   * @param timestamps - a list of timestamps
   * @param searchedTimestamp - a specific timestamp to be found
   * @return an Optional containing the retrieved timestamp or empty
   */
  private Optional<Timestamp> getTimestampPosition(final List<Timestamp> timestamps,
      final long searchedTimestamp) {

    for (final Timestamp t : timestamps) {
      if (t.getTimestamp() == searchedTimestamp) {
        return Optional.of(t);
      }
    }
    return Optional.empty();
  }


}
