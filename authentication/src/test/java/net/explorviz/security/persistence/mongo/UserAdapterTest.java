package net.explorviz.security.persistence.mongo;

import static org.junit.Assert.assertEquals;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.Arrays;
import java.util.List;
import net.explorviz.shared.security.User;
import org.junit.Test;

/**
 * Unit tests for {@link UserAdapter}.
 *
 */
@SuppressWarnings("PMD")
public class UserAdapterTest {


  @Test
  public void testToDbObject() {
    final List<String> roles = Arrays.asList("admin", "tester");
    final User u = new User(12L, "username", "password", roles);

    final UserAdapter adapter = new UserAdapter();
    final DBObject userDbObject = adapter.toDbObject(u);

    assertEquals(12L, userDbObject.get("id"));
    assertEquals("username", userDbObject.get("username"));
    assertEquals("password", userDbObject.get("password"));
    assertEquals(roles, userDbObject.get("roles"));
  }

  @Test
  public void testFromDbObject() {
    final BasicDBObject userDbObject = new BasicDBObject();
    final List<String> roles = Arrays.asList("admin", "tester");
    userDbObject.append("id", 17L).append("username", "name").append("password", "pw")
        .append("roles", roles);

    final UserAdapter userAdapter = new UserAdapter();

    final User u = userAdapter.fromDbObject(userDbObject);

    assertEquals(17L, (long) u.getId());
    assertEquals("name", u.getUsername());
    assertEquals("pw", u.getPassword());
    assertEquals(roles, u.getRoles());

  }

}
