package net.explorviz.landscape.repository.persistence;

import java.util.Map;
import net.explorviz.landscape.model.landscape.Landscape;

/**
 * Represents a repository to store {@link Landscape} objects persistently.
 *
 */
public interface LandscapeRepository {


  /**
   * Saves a {@link Landscape} object in the repository.
   *
   * @param timestamp the timestamp, associated to this landscape. Will be used to index the
   *        objects.
   * @param landscape the landscape object.
   */
  void saveLandscape(final long timestamp, Landscape landscape);


  /**
   * Save a landscape as replay in the repository.
   *
   * @param timestamp the timestamp
   * @param replay the replay landscape
   */
  default void saveReplay(final long timestamp, final Landscape replay) {
    throw new RuntimeException("Not implemented");
  }

  /**
   * Retrieves a landscape object with a specific timestamp from the repository.
   *
   * @param timestamp the timestamp of the landscape object
   *
   * @return the landscape object
   */
  Landscape getLandscapeByTimestamp(final long timestamp);


  /**
   * Retrieves a landscape object with a specific, unique identifier.
   *
   * @param id the id of the landscape object
   *
   * @return the landscape object
   */
  Landscape getLandscapeById(final String id);



  /**
   *
   * Retrieves a replay landscape object with a specific timestamp from the repository.
   *
   * @param timestamp the timestamp of the replay
   *
   * @return the landscape object
   */
  default Landscape getReplayByTimestamp(final long timestamp) {
    throw new RuntimeException("Not implemented");
  }

  /**
   * Retrieves a replay object with a specific, unique identifier.
   *
   * @param id the id of the landscape object
   *
   * @return the replay object
   */
  default Landscape getReplayById(final String id) {
    throw new RuntimeException("Not implemented");
  }

  /**
   * Returns all landscape model snapshots for timeshift use.
   *
   * @return a map containing the timestamps as keys and activity(?) as values.
   */
  Map<Long, Long> getAllForTimeshift();


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
