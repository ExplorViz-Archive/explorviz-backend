package net.explorviz.history.repository.persistence;

import java.util.Optional;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.store.Timestamp;

/**
 * Represents a repository to store {@link Landscape} objects persistently for replay.
 *
 * @param <T> the return type of landscapes.
 *
 */
public interface ReplayRepository<T> {


  /**
   * Save a landscape as replay in the repository.
   *
   * @param timestamp the timestamp
   * @param replay the replay landscape
   * @param totalRequests the total amount of requests
   */
  void save(final long timestamp, final Landscape replay, int totalRequests);

  /**
   * Retrieves the total requests of a replay.
   *
   * @param timestamp the timestamp of the replay
   * @return the total requests
   */
  int getTotalRequestsByTimestamp(long timestamp);


  /**
   * Retrieves a replay landscape object with a specific timestamp from the repository.
   *
   * @param timestamp the timestamp of the replay
   *
   * @return the landscape object
   */
  Optional<T> getByTimestamp(final long timestamp);


  /**
   * Retrieves a replay landscape object with a specific timestamp from the repository.
   *
   * @param timestamp the timestamp of the replay
   *
   * @return the landscape object
   */
  Optional<T> getByTimestamp(final Timestamp timestamp);

  /**
   * Retrieves a replay object with a specific, unique identifier.
   *
   * @param id the id of the landscape object
   *
   * @return the replay object
   */
  Optional<T> getById(final String id);

  /**
   * Removes all landscapes that have exceeded their lifespan.
   *
   * @param from the reference timestamp to use for lifetime calculation.
   */
  void cleanup(final long from);

  /**
   * Removes all landscapes that have exceeded their life span. Equivalent to
   * {@code cleanup(System.currentTimeMillis())}
   *
   */
  default void cleanup() {
    cleanup(System.currentTimeMillis());
  }

  /**
   * Removes all landscapes in the repository.
   */
  void clear();


}
