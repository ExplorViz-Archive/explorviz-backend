package net.explorviz.shared.persistence.mongo.adapter;

import com.mongodb.DBObject;

public interface MongoAdapter<T> {

  DBObject toDBObject(T entity);

  T fromDBObject(DBObject entity);

}
