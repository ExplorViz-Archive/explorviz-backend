package net.explorviz.history.repository.persistence;

import java.util.Optional;
import javax.ws.rs.ClientErrorException;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.store.Timestamp;

/**
 * Represents a repository to store {@link Landscape} objects persistently.
 *
 * @param <T> the return type of landscapes.
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
  void save(final Long timestamp, Landscape landscape, int totalRequests);



  /**
   * Retrieves a landscape object with a specific timestamp from the repository.
   *
   * @param timestamp the timestamp of the landscape object
   *
   * @return the landscape object
   *
   * @throws ClientErrorException if the landscape could not be found.
   */
  Optional<T> getByTimestamp(final long timestamp);

  /**
   * Retrieves a landscape object with a specific timestamp from the repository.
   *
   * @param timestamp the timestamp of the landscape object
   *
   * @return the landscape object
   *
   * @throws ClientErrorException if the landscape could not be found.
   */
  Optional<T> getByTimestamp(final Timestamp timestamp);



  /**
   * Retrieves a landscape object with a specific, unique identifier.
   *
   * @param id the id of the landscape object
   *
   * @return the landscape object
   *
   * @throws ClientErrorException if the landscape could not be found.
   */
  Optional<T> getById(final String id);


  /**
   * Retrieves the total requests of a landscape.
   *
   * @param timestamp the timestamp of the landscape
   * @return the total requests
   */
  int getTotalRequests(long timestamp);


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
