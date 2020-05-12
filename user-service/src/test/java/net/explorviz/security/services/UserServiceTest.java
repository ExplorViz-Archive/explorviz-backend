package net.explorviz.security.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.MultivaluedHashMap;
import net.explorviz.security.services.exceptions.UserCrudException;
import net.explorviz.security.user.Role;
import net.explorviz.security.user.User;
import net.explorviz.security.util.PasswordStorage;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.querying.Query;
import net.explorviz.shared.querying.QueryResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import xyz.morphia.Datastore;
import xyz.morphia.query.FindOptions;

// CHECKSTYLE.OFF: MultipleStringLiteralsCheck
// CHECKSTYLE.OFF: MagicNumberCheck


/**
 * Unit test for {@link UserService}.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  private UserService userService;

  private List<User> users;

  @Mock
  private Datastore store;

  @Mock
  private IdGenerator idGen;

  @Mock
  private KafkaUserService kus;

  private void fillUsers(final int size) {
    this.users = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      final User u = new User(String.valueOf(i), "user-" + i, "abc", null);
      this.users.add(u);
    }
  }

  @BeforeEach
  public void setUp() {
    this.userService = new UserService(this.store, idGen, kus);
  }

  @Test
  public void testPaginationFull() {
    final int usersTotal = 13;
    final int pageLen = 6;
    final int pageIndex = 1;
    this.fillUsers(usersTotal);

    final MultivaluedHashMap<String, String> paginationParams = new MultivaluedHashMap<>();
    paginationParams.add("page[number]", String.valueOf(pageIndex));
    paginationParams.add("page[size]", String.valueOf(pageLen));
    final Query<User> paginationQuery = Query.fromParameterMap(paginationParams);

    final xyz.morphia.query.Query<User> q = Mockito.mock(xyz.morphia.query.Query.class);
    Mockito.when(this.store.createQuery(User.class)).thenReturn(q);
    Mockito.doAnswer((Answer<List<User>>) invocation -> {
      final FindOptions options = invocation.getArgument(0, FindOptions.class);
      final int skip = options.getSkip();
      final int limit = options.getLimit();
      return new ArrayList<>(users.subList(skip, skip + limit));
    }).when(q).asList(ArgumentMatchers.any(FindOptions.class));

    final QueryResult<User> returned = this.userService.query(paginationQuery);

    Assert.assertEquals(pageLen, returned.getN());
  }

  @Test
  public void testChangePassword() throws PasswordStorage.CannotPerformOperationException,
      PasswordStorage.InvalidHashException, UserCrudException {

    final String id = RandomStringUtils.random(5);
    Mockito.when(idGen.generateId()).thenReturn(id);
    final User u1 = new User("testuser");
    u1.setPassword("password");


    final User newUser = this.userService.saveNewEntity(u1);
    Mockito.when(store.get(User.class, id)).thenReturn(newUser);

    final String uid = newUser.getId();

    final User update = new User(null, null, "newpw", null);

    final User updatedUser = this.userService.updateEntity(uid, update);

    assertTrue(PasswordStorage.verifyPassword("newpw", updatedUser.getPassword()));
    assertEquals(newUser.getId(), updatedUser.getId());
    assertEquals(u1.getUsername(), updatedUser.getUsername());
    assertEquals(u1.getRoles(), updatedUser.getRoles());
  }

  @Test
  public void testChangeUsername() throws UserCrudException {


    final String id = RandomStringUtils.random(5);
    Mockito.when(idGen.generateId()).thenReturn(id);
    final User u1 = new User("testuser");
    u1.setPassword("password");

    final User newUser = this.userService.saveNewEntity(u1);
    Mockito.when(store.get(User.class, id)).thenReturn(newUser);

    final String uid = newUser.getId();

    final User update = new User(null, "newname", null, null);
    final User updatedUser = this.userService.updateEntity(uid, update);

    assertEquals("newname", updatedUser.getUsername());
    assertEquals(newUser.getId(), updatedUser.getId());
    assertEquals(u1.getRoles(), updatedUser.getRoles());
    assertEquals(u1.getPassword(), updatedUser.getPassword());
  }

  @Test
  public void testChangeRoles() throws UserCrudException {
    final String id = RandomStringUtils.random(5);
    Mockito.when(idGen.generateId()).thenReturn(id);
    final User u1 = new User("testuser");
    u1.setPassword("password");

    final User newUser = this.userService.saveNewEntity(u1);
    Mockito.when(store.get(User.class, id)).thenReturn(newUser);

    final String uid = newUser.getId();

    final User update =
        new User(null, null, null,
            Collections.singletonList(Role.USER_NAME));
    final User updatedUser = this.userService.updateEntity(uid, update);

    assertTrue(updatedUser.getRoles().stream().anyMatch(r -> r.equals(Role.USER_NAME)));
    assertEquals(newUser.getId(), updatedUser.getId());
    assertEquals(u1.getUsername(), updatedUser.getUsername());
    assertEquals(u1.getPassword(), updatedUser.getPassword());
  }


}
