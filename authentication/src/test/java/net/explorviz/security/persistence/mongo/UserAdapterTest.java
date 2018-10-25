package net.explorviz.security.persistence.mongo;

import static org.junit.Assert.assertEquals;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.Arrays;
import java.util.List;
import net.explorviz.security.persistence.mongo.UserAdapter;
import net.explorviz.shared.security.User;
import org.junit.Test;

public class UserAdapterTest {

  @Test
  public void testToDBObject() {
    final List<String> roles = Arrays.asList("admin", "tester");
    final User u = new User(12L, "username", "password", roles);

    final UserAdapter adapter = new UserAdapter();
    final DBObject userDBObject = adapter.toDBObject(u);

    assertEquals(12L, userDBObject.get("_id"));
    assertEquals("username", userDBObject.get("username"));
    assertEquals("password", userDBObject.get("password"));
    assertEquals(roles, userDBObject.get("roles"));
  }

  @Test
  public void testFromDBObject() {
    final BasicDBObject userDBObject = new BasicDBObject();
    final List<String> roles = Arrays.asList("admin", "tester");
    userDBObject.append("_id", 17).append("username", "name").append("password", "pw")
        .append("roles", roles);

    final UserAdapter userAdapter = new UserAdapter();

    final User u = userAdapter.fromDBObject(userDBObject);

    assertEquals(17L, (long) u.getId());
    assertEquals("name", u.getUsername());
    assertEquals("pw", u.getPassword());
    assertEquals(roles, u.getRoles());

  }

}
