package net.explorviz.landscape.repository.persistence;

import javax.ws.rs.ClientErrorException;
import net.explorviz.landscape.model.landscape.Landscape;

/**
 * Represents a repository to store {@link Landscape} objects persistently.
 *
 * @param T the return type of landscapes.
 * @param I the type of the id.
 *
 */
public interface LandscapeRepository<T> {


  /**
   * Saves a {@link Landscape} object in the repository.
   *
   * @param timestamp the timestamp, associated to this landscape. Will be used to index the
   *        objects.
   * @param landscape the landscape object.
   * @param totalRequests the total amount of requests
   */
  void saveLandscape(final long timestamp, Landscape landscape, long totalRequests);


  /**
   * Save a landscape as replay in the repository.
   *
   * @param timestamp the timestamp
   * @param replay the replay landscape
   * @param totalRequests the total amount of requests
   */
  void saveReplay(final long timestamp, final Landscape replay, long totalRequests);

  /**
   * Retrieves a landscape object with a specific timestamp from the repository.
   *
   * @param timestamp the timestamp of the landscape object
   *
   * @return the landscape object
   *
   * @throws ClientErrorException if the landscape could not be found.
   */
  T getLandscapeByTimestamp(final long timestamp);


  /**
   * Retrieves a landscape object with a specific, unique identifier.
   *
   * @param id the id of the landscape object
   *
   * @return the landscape object
   *
   * @throws ClientErrorException if the landscape could not be found.
   */
  T getLandscapeById(final long id);


  /**
   * Retrieves the total requests of a landscape.
   *
   * @param timestamp the timestamp of the landscape
   * @return the total requests
   */
  long getLandscapeTotalRequests(long timestamp);


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
