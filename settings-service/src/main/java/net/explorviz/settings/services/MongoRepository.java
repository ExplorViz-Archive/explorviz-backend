package net.explorviz.settings.services;

import java.util.List;
import java.util.Optional;

/**
 * Defines a repository for persistent CRUD operations.
 *
 * @param <T> the root type this repository handles
 * @param <K> the key type
 */
public interface MongoRepository<T, K> {

  /**
   * Persists a new object.
   *
   * @param entity the object to persist
   */
  T createOrUpdate(T entity);

  /**
   * Searches the repository for an entity with the given id.
   *
   * @param key the key
   * @return the object
   */
  Optional<T> find(K key);

  /**
   * Searches all objects within the repository.
   *
   * @return a list of all persistent objects
   */
  List<T> findAll();


  /**
   * Deletes an object.
   *
   * @param key the key of the object to delete
   */
  void delete(K key);



}
