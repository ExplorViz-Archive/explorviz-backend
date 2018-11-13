package net.explorviz.security.persistence.mongo;

import com.mongodb.DBObject;

/**
 * Defines the interface to parse model entities to MongoDB objects and vice versa.
 *
 * @param <T> type of the entity
 */
public interface MongoAdapter<T> {

  DBObject toDbObject(T entity);

  T fromDbObject(DBObject entity);

}
