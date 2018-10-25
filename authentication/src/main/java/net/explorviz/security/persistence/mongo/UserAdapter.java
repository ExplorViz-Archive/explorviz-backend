package net.explorviz.security.persistence.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.List;
import net.explorviz.shared.security.User;

public class UserAdapter implements MongoAdapter<User> {

  @Override
  public DBObject toDBObject(final User entity) {
    final BasicDBObject dbObject = new BasicDBObject();

    if (entity.getId() != null) {
      dbObject.append("_id", entity.getId());
    }

    dbObject.append("username", entity.getUsername());
    dbObject.append("password", entity.getPassword());
    dbObject.append("roles", entity.getRoles());

    return dbObject;
  }

  @Override
  public User fromDBObject(final DBObject entity) {
    final String name = (String) entity.get("username");
    final String password = (String) entity.get("password");
    final Long id = ((Integer) entity.get("_id")).longValue();
    final List<String> roles = (List<String>) entity.get("roles");

    return new User(id, name, password, roles);

  }

}
