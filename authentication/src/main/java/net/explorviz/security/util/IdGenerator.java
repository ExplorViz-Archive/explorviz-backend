package net.explorviz.security.util;

/**
 * Generates ids of type {@code T}. Can be employed, if the underlying dbms does not auto generate
 * ids.
 *
 * @param <T> the type of the ids to generate
 */
public interface IdGenerator<T> {

  /**
   * Creates an id.
   *
   * @return the freshly generated id
   */
  T next();

}
