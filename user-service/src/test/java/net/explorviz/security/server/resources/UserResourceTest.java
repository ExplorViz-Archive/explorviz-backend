package net.explorviz.security.server.resources;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.UriInfo;
import net.explorviz.security.services.UserService;
import net.explorviz.security.services.exceptions.UserCrudException;
import net.explorviz.security.user.User;
import net.explorviz.security.util.PasswordStorage;
import net.explorviz.security.util.PasswordStorage.CannotPerformOperationException;
import net.explorviz.security.util.PasswordStorage.InvalidHashException;
import net.explorviz.shared.querying.Query;
import net.explorviz.shared.querying.QueryResult;
import net.explorviz.security.user.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link UserResource}. All tests are performed by just calling the methods of
 * {@link UserResource}.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
class UserResourceTest {

  @InjectMocks
  private UserResource userResource;

  @Mock
  private UserService userCrudService;

  private final Map<String, User> users = new HashMap<>();

  private final List<String> roles = new ArrayList<>();

  private Long lastId = 0L;

  @BeforeEach
  public void setUp() throws UserCrudException {



    Mockito.lenient().when(this.userCrudService.saveNewEntity(any())).thenAnswer(inv -> {
      final User u = (User) inv.getArgument(0);
      final String id = Long.toString(++this.lastId);
      final User newUser = new User(id, u.getUsername(), u.getPassword(), u.getRoles());
      this.users.put(id, newUser);
      return newUser;
    });

    Mockito.lenient().when(this.userCrudService.getEntityById(any())).thenAnswer(inv -> {
      return Optional.ofNullable(this.users.get(inv.getArgument(0)));
    });

    Mockito.lenient().doAnswer(inv -> {
      this.users.remove(inv.getArgument(0));
      return null;
    }).when(this.userCrudService).deleteEntityById(any());

    Mockito.lenient().when(this.userCrudService.query(any())).thenAnswer(inv -> {
      final Query<User> query = (Query<User>) inv.getArgument(0);
      Collection<User> data = this.users.values();
      if (query.doFilter()) {
        final String role = query.getFilters().get("role").get(0);
        data = this.users.values()
            .stream()
            .filter(u -> u.getRoles().stream().anyMatch(r -> r.equals(role)))
            .collect(Collectors.toList());
      }
      return new QueryResult<>(inv.getArgument(0), data, data.size());

    });

    Mockito.lenient().when(this.userCrudService.getAll()).thenAnswer(inv -> {
      return this.users.values().stream().collect(Collectors.toList());
    });

  }

  @AfterEach
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



    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    final UriInfo uri = Mockito.mock(UriInfo.class);
    Mockito.when(uri.getQueryParameters(true)).thenReturn(params);

    final Collection<User> data = this.userResource.find(uri).getData();

    assertEquals(2, data.size());
  }

  @Test
  public void testNewUser() {
    final User u = new User("testuser");
    u.setPassword("testPassword");

    final User newUser = this.userResource.newUser(u);

    assertNotNull(newUser.getId());
    assertTrue(Long.parseLong(newUser.getId()) > 0);
  }


  @Test
  public void testInvalidUsername() {
    final User u = new User("");

    assertThrows(BadRequestException.class, () -> this.userResource.newUser(u));

  }

  @Test
  public void testInvalidPassword() {
    final User u = new User("");
    u.setPassword("");
    assertThrows(BadRequestException.class, () -> this.userResource.newUser(u));
  }

  @Test
  public void testInvalidId() {
    final User u = new User("12", "name", "pw", new ArrayList<>());
    assertThrows(BadRequestException.class, () -> this.userResource.newUser(u));
  }

  @Test
  public void testInvalidRoles() {
    final User u = new User(null, "name", "pw", Collections.singletonList("unknown"));
    assertThrows(BadRequestException.class, () -> this.userResource.newUser(u));
  }



  @Test
  public void testUserByRole() {


    final User u1 = new User("testuser");
    u1.setPassword("password");
    u1.setRoles(Arrays.asList(Role.USER_NAME));

    final User u2 = new User("testuser2");
    u2.setPassword("password");
    u2.setRoles(Arrays.asList(Role.USER_NAME));

    this.userResource.newUser(u1);
    this.userResource.newUser(u2);

    final MultivaluedHashMap<String, String> roleparams = new MultivaluedHashMap<>();
    roleparams.add("filter[role]", "user");
    final UriInfo uri = Mockito.mock(UriInfo.class);
    Mockito.when(uri.getQueryParameters(true)).thenReturn(roleparams);

    assertEquals(2, this.userResource.find(uri).getData().size());
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
    final String uid = newUser.getId();

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
    final String uid = newUser.getId();

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

    final String uid = newUser.getId();

    final User update = new User(null, null, null, Arrays.asList(Role.USER_NAME));
    final User updatedUser = this.userResource.updateUser(uid, update);

    assertTrue(updatedUser.getRoles().stream().anyMatch(r -> r.equals(Role.USER_NAME)));
    assertEquals(newUser.getId(), updatedUser.getId());
    assertEquals(u1.getUsername(), updatedUser.getUsername());
    assertEquals(u1.getPassword(), updatedUser.getPassword());
  }



}
