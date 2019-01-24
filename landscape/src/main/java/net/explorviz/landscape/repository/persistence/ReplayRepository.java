package net.explorviz.landscape.repository.persistence;

import net.explorviz.landscape.model.landscape.Landscape;

public interface ReplayRepository<T> {


  /**
   * Save a landscape as replay in the repository.
   *
   * @param timestamp the timestamp
   * @param replay the replay landscape
   * @param totalRequests the total amount of requests
   */
  void saveReplay(final long timestamp, final Landscape replay, long totalRequests);

  /**
   * Retrieves the total requests of a replay.
   *
   * @param timestamp the timestamp of the replay
   * @return the total requests
   */
  long getReplayTotalRequests(long timestamp);

  /**
   * Retrieves a replay landscape object with a specific timestamp from the repository.
   *
   * @param timestamp the timestamp of the replay
   *
   * @return the landscape object
   */
  T getReplayByTimestamp(final long timestamp);

  /**
   * Retrieves a replay object with a specific, unique identifier.
   *
   * @param id the id of the landscape object
   *
   * @return the replay object
   */
  T getReplayById(final long id);

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
