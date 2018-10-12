package net.explorviz.security.server.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.explorviz.security.model.Password;
import net.explorviz.security.services.UserCrudService;
import net.explorviz.shared.security.User;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {

  @InjectMocks
  private UserResource userResource;

  @Mock
  private UserCrudService userCrudService;

  private final Map<Long, User> users = new HashMap<>();
  private Long lastId = 0L;

  @Before
  public void setUp() {

    when(this.userCrudService.saveNewUser(any())).thenAnswer(inv -> {
      final User u = (User) inv.getArgument(0);
      final long id = ++this.lastId;
      final User newUser = new User(id, u.getUsername(), u.getPassword(), u.getRoles());
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
  public void tearDown() {
    this.users.clear();
  }


  @Test
  public void testNewUser() {
    final User u = new User("testuser");
    u.setPassword("testPassword");

    final User newUser = this.userResource.newUser(u);

    assertNotNull(newUser.getId());
    assertTrue(newUser.getId() > 0);

  }

  @Test
  public void testUserByRole() {
    final User u1 = new User("testuser");
    u1.setPassword("password");
    u1.setRoles(Arrays.asList("role1", "role2"));

    final User u2 = new User("testuser2");
    u2.setPassword("password");
    u2.setRoles(Arrays.asList("role1"));

    this.userResource.newUser(u1);
    this.userResource.newUser(u2);

    final List<User> role1Users = this.userResource.usersByRole("role1");
    assertEquals(2, role1Users.size());

    final List<User> role2Users = this.userResource.usersByRole("role2");
    assertEquals(1, role2Users.size());

    final List<User> role3Users = this.userResource.usersByRole("role3");
    assertEquals(0, role3Users.size());

  }

  @Test
  public void testRemoveUser() {
    final User u1 = new User("testuser");
    u1.setPassword("password");
    final User newUser = this.userResource.newUser(u1);

    this.userResource.removeUser(newUser.getId());
  }

  @Test
  public void testChangePassword() {

    // Will always fail if passwords are hashed

    final User u1 = new User("testuser");
    u1.setPassword("password");
    final User newUser = this.userResource.newUser(u1);

    this.userResource.changePassword(newUser.getId(), new Password("newpassword"));

    final User changePwUser = this.userResource.userById(newUser.getId());
    assertEquals("newpassword", changePwUser.getPassword());
  }

  @Test
  public void testUserRoles() {
    final User u1 = new User("testuser");
    u1.setPassword("password");
    u1.setRoles(Arrays.asList("role1", "role2"));
    final User newUser = this.userResource.newUser(u1);

    final List<String> roles = this.userResource.userRoles(newUser.getId());

    assertThat(roles, CoreMatchers.hasItems("role1", "role2"));
  }

  @Test
  public void testChangeRoles() {
    // todo
  }

}
