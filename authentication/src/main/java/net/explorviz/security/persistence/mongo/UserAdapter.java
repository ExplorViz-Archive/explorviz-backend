package net.explorviz.security.persistence.mongo;

// CHECKSTYLE.OFF: CustomImportOrderCheck

import static net.explorviz.security.persistence.mongo.FieldHelper.FIELD_ID;
import static net.explorviz.security.persistence.mongo.FieldHelper.FIELD_PASSWORD;
import static net.explorviz.security.persistence.mongo.FieldHelper.FIELD_ROLES;
import static net.explorviz.security.persistence.mongo.FieldHelper.FIELD_USERNAME;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.List;
import net.explorviz.shared.security.User;

/**
 * Adapter to parse {@link User} objects and to MongoDB objects and vice versa.
 *
 */
public class UserAdapter implements MongoAdapter<User> {



  @Override
  public DBObject toDbObject(final User entity) {
    final BasicDBObject dbObject = new BasicDBObject();

    if (entity.getId() != null) {
      dbObject.append(FIELD_ID, entity.getId());
    }

    dbObject.append(FIELD_USERNAME, entity.getUsername());
    dbObject.append(FIELD_PASSWORD, entity.getPassword());
    dbObject.append(FIELD_ROLES, entity.getRoles());

    return dbObject;
  }

  @Override
  public User fromDbObject(final DBObject entity) {
    final String name = (String) entity.get(FIELD_USERNAME);
    final String password = (String) entity.get(FIELD_PASSWORD);
    final Long id = (Long) entity.get(FIELD_ID);
    final List<String> roles = (List<String>) entity.get(FIELD_ROLES);

    return new User(id, name, password, roles);

  }

}
