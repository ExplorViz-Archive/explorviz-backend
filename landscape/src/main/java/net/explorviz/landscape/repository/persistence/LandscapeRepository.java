package net.explorviz.landscape.repository.persistence;

import java.util.Map;
import net.explorviz.landscape.model.landscape.Landscape;

/**
 * Represents a repository to store {@link Landscape} objects persistently.
 *
 */
public interface LandscapeRepository {


  /**
   * Saves a {@link Landscape} object to the repository.
   *
   * @param timestamp the timestamp, associated to this landscape. Will be used to index the
   *        objects.
   * @param landscape the landscape object.
   */
  void save(final long timestamp, Landscape landscape);

  /**
   * Retrieves a landscape object with a specific timestamp from the repository.
   *
   * @param timestamp the timestamp of the landscape object
   *
   * @return the landscape object
   */
  Landscape getByTimestamp(final long timestamp);

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
  void cleanup();

  /**
   * Removes all landscapes in the repository.
   */
  void clear();

}
