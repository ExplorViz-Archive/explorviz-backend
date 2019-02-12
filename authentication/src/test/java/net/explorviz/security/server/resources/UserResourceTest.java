package net.explorviz.security.server.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.BadRequestException;
import net.explorviz.security.services.RoleService;
import net.explorviz.security.services.UserMongoCrudService;
import net.explorviz.security.util.PasswordStorage;
import net.explorviz.security.util.PasswordStorage.CannotPerformOperationException;
import net.explorviz.security.util.PasswordStorage.InvalidHashException;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests for {@link UserResource}. All tests are performed by just calling the methods of
 * {@link UserResource}. See {@link UserResourceEndpointTest} for tests that use web requests.
 *
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD")
public class UserResourceTest {

  @InjectMocks
  private UserResource userResource;

  @Mock
  private UserMongoCrudService userCrudService;

  @Mock
  private RoleService roleService;


  private final Map<Long, User> users = new HashMap<>();

  private final List<Role> roles = new ArrayList<>();

  private Long lastId = 0L;

  @Before
  public void setUp() {



    when(this.roleService.getAllRoles()).thenReturn(this.roles);


    when(this.userCrudService.saveNewEntity(any())).thenAnswer(inv -> {
      final User u = (User) inv.getArgument(0);
      final long id = ++this.lastId;
      final User newUser = new User(id, u.getUsername(), u.getPassword(), u.getRoles());
      this.users.put(id, newUser);
      return Optional.ofNullable(newUser);
    });

    when(this.userCrudService.getEntityById(any())).thenAnswer(inv -> {
      return Optional.ofNullable(this.users.get(inv.getArgument(0)));
    });

    doAnswer(inv -> {
      this.users.remove(inv.getArgument(0));
      return null;
    }).when(this.userCrudService).deleteEntityById(any());

    when(this.userCrudService.getUsersByRole(any())).thenAnswer(inv -> {
      final String role = inv.getArgument(0);
      return this.users.values().stream()
          .filter(u -> u.getRoles().stream().anyMatch(r -> r.getDescriptor().equals(role)))
          .collect(Collectors.toList());

    });

    when(this.userCrudService.getAll()).thenAnswer(inv -> {
      return this.users.values().stream().collect(Collectors.toList());
    });

  }

  @After
  public void tearDown() {
    this.users.clear();
    this.roles.clear();
  }


  @Test
  public void testGetAll() {
    final User u = new User("testuser");
    u.setPassword("testPassword");

    this.userResource.newUser(u);

    final User u2 = new User("testuser");
    u2.setPassword("testPassword");
    this.userResource.newUser(u2);

    final List<User> usersFound = this.userResource.usersByRole(null);
    assertEquals(2, usersFound.size());

  }

  @Test
  public void testNewUser() {
    final User u = new User("testuser");
    u.setPassword("testPassword");

    final User newUser = this.userResource.newUser(u);

    assertNotNull(newUser.getId());
    assertTrue(newUser.getId() > 0);
  }


  @Test(expected = BadRequestException.class)
  public void testInvalidUsername() {
    final User u = new User("");
    this.userResource.newUser(u);
  }

  @Test(expected = BadRequestException.class)
  public void testInvalidPassword() {
    final User u = new User("");
    u.setPassword("");
    this.userResource.newUser(u);
  }

  @Test(expected = BadRequestException.class)
  public void testInvalidId() {
    final User u = new User(12L, "name", "pw", new ArrayList<>());
    this.userResource.newUser(u);
  }


  @Test
  public void testListOfNewUsers() {
    this.roles.add(new Role(1L, "role"));
    final List<Role> roles = Arrays.asList(new Role(1L, "role"));
    final User u1 = new User(null, "u1", "pw", roles);
    final User u2 = new User(null, "u2", "pw", roles);
    final User u3 = new User(null, "u3", "pw", roles);

    this.userResource.createAll(Arrays.asList(u1, u2, u3));

    final List<User> created = this.userCrudService.getUsersByRole("role");

    // Check if 3 users where created
    assertEquals(3, created.size());
  }

  @Test
  public void testListOfNewUsersWithInvalidUser() {
    this.roles.add(new Role(1L, "role"));
    final List<Role> roles = Arrays.asList(new Role(1L, "role"));
    final User u1 = new User(null, "u1", "", roles);
    final User u2 = new User(null, "u2", "pw", roles);
    final User u3 = new User(null, "u3", "pw", roles);

    this.userResource.createAll(Arrays.asList(u1, u2, u3));

    final List<User> created = this.userCrudService.getUsersByRole("role");

    // Check if 3 users where created
    assertEquals(2, created.size());
  }

  @Test
  public void testUserByRole() {

    this.roles.add(new Role(1L, "role1"));
    this.roles.add(new Role(2L, "role2"));
    this.roles.add(new Role(3L, "role3"));

    final User u1 = new User("testuser");
    u1.setPassword("password");
    u1.setRoles(Arrays.asList(new Role(1L, "role1"), new Role(2L, "role2")));

    final User u2 = new User("testuser2");
    u2.setPassword("password");
    u2.setRoles(Arrays.asList(new Role(1L, "role1")));

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
  public void testChangePassword() throws CannotPerformOperationException, InvalidHashException {

    // Will always fail if passwords are hashed

    final User u1 = new User("testuser");
    u1.setPassword("password");
    final User newUser = this.userResource.newUser(u1);
    final long uid = newUser.getId();

    final User update = new User(null, null, "newpw", null);

    final User updatedUser = this.userResource.updateUser(uid, update);

    assertTrue(PasswordStorage.verifyPassword("newpw", updatedUser.getPassword()));
    assertEquals(newUser.getId(), updatedUser.getId());
    assertEquals(u1.getUsername(), updatedUser.getUsername());
    assertEquals(u1.getRoles(), updatedUser.getRoles());
  }


  @Test
  public void testChangeUsername() {
    final User u1 = new User("testuser");
    u1.setPassword("password");
    final User newUser = this.userResource.newUser(u1);
    final long uid = newUser.getId();

    final User update = new User(null, "newname", null, null);
    final User updatedUser = this.userResource.updateUser(uid, update);

    assertEquals("newname", updatedUser.getUsername());
    assertEquals(newUser.getId(), updatedUser.getId());
    assertEquals(u1.getRoles(), updatedUser.getRoles());
    assertEquals(u1.getPassword(), updatedUser.getPassword());
  }


  @Test
  public void testChangeRoles() {
    final User u1 = new User("testuser");
    u1.setPassword("password");
    final User newUser = this.userResource.newUser(u1);

    final long uid = newUser.getId();

    final User update = new User(null, null, null, Arrays.asList(new Role(3L, "newrole")));
    this.roles.add(new Role(3L, "newrole"));
    final User updatedUser = this.userResource.updateUser(uid, update);

    assertTrue(updatedUser.getRoles().stream().anyMatch(r -> r.getDescriptor().equals("newrole")));
    assertEquals(newUser.getId(), updatedUser.getId());
    assertEquals(u1.getUsername(), updatedUser.getUsername());
    assertEquals(u1.getPassword(), updatedUser.getPassword());
  }


  @Test(expected = BadRequestException.class)
  public void testUnknownSettings() {
    final User u = new User("testuser");
    u.setPassword("password");
    u.getSettings().getBooleanAttributes().put("UnknownKey", false);
    this.userResource.newUser(u);
  }


  @Test(expected = BadRequestException.class)
  public void testSettingsNotInRange() {
    final User u = new User("testuser");
    u.setPassword("password");
    u.getSettings().getNumericAttributes().put("appVizTransparencyIntensity", 0.7);
    this.userResource.newUser(u);
  }


}
