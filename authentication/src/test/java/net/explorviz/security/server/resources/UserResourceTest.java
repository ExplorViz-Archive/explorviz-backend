package net.explorviz.security.server.resources;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.explorviz.security.services.UserCrudService;
import net.explorviz.shared.security.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class UserResourceTest {

  @InjectMocks
  private UserResource userResource;

  @Mock
  private UserCrudService userCrudService;

  private final Map<Long, User> users = new HashMap<>();
  private Long lastId = 0L;

  @Before
  public void setup() {

    when(this.userCrudService.saveNewUser(any())).thenAnswer(inv -> {
      final User u = (User) inv.getArgument(0);
      final long id = this.lastId++;
      final User newUser = new User(id, u.getUsername(), u.getPassword());
      this.users.put(id, newUser);
      return newUser;
    });

    when(this.userCrudService.getUserById(any())).thenAnswer(inv -> {
      return this.users.get(inv.getArgument(0));
    });

    doAnswer(inv -> {
      this.users.remove(inv.getArgument(0));
      return null;
    }).when(this.userCrudService).deleteUserById(any());

    when(this.userCrudService.getUsersByRole(any())).thenAnswer(inv -> {
      final String role = inv.getArgument(0);
      return this.users.values().stream().filter(u -> u.getRoles().contains(role))
          .collect(Collectors.toList());

    });

  }

  @After
  public void teadown() {
    this.users.clear();
  }


  @Test
  public void testNewUser() {
    final User u = new User("testuser");
    u.setPassword("testPassword");

    this.userResource.newUser(u);

    assertNotNull(u.getId());
    assertTrue(u.getId() > 0);

  }

  @Test
  public void testUserByRole() {
    fail("Not yet implemented");
  }

  @Test
  public void testRemoveUser() {
    fail("Not yet implemented");
  }

  @Test
  public void testChangePassword() {
    fail("Not yet implemented");
  }

  @Test
  public void testUserRoles() {
    fail("Not yet implemented");
  }

  @Test
  public void testChangeRoles() {
    fail("Not yet implemented");
  }

}
