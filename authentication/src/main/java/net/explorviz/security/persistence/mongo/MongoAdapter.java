package net.explorviz.security.persistence.mongo;

import com.mongodb.DBObject;

public interface MongoAdapter<T> {

  DBObject toDBObject(T entity);

  T fromDBObject(DBObject entity);

}
