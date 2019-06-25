package net.explorviz.security.services;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.shared.querying.Query;
import net.explorviz.shared.querying.QueryResult;
import net.explorviz.shared.security.model.User;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import xyz.morphia.Datastore;
import xyz.morphia.query.FindOptions;

/**
 * Unit test for {@link UserService}.
 *
 *
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  private UserService userService;

  private List<User> users;

  @Mock
  private Datastore store;

  private void fillUsers(final int size) {
    this.users = new ArrayList<>();
    final String prefix = "user-";
    for (int i = 0; i < size; i++) {
      final User u = new User("" + i, prefix + i, "abc", null);
      this.users.add(u);
    }
  }

  @BeforeEach
  public void setUp() {
    this.userService = new UserService(this.store, null);
  }

  @Test
  public void testPaginationFull() {
    final int usersTotal = 13;
    final int pageLen = 6;
    final int pageIndex = 1;
    this.fillUsers(usersTotal);

    final Query<User> paginationQuery = new Query<User>() {

      @Override
      public Integer getPageLength() {
        // TODO Auto-generated method stub
        return pageLen;
      }

      @Override
      public Integer getPageIndex() {
        // TODO Auto-generated method stub
        return pageIndex;
      }
    };

    final xyz.morphia.query.Query<User> q = Mockito.mock(xyz.morphia.query.Query.class);
    Mockito.when(this.store.createQuery(User.class)).thenReturn(q);
    Mockito.doAnswer(new Answer<List<User>>() {

      @Override
      public List<User> answer(final InvocationOnMock invocation) throws Throwable {
        final FindOptions options = invocation.getArgument(0, FindOptions.class);
        final int skip = options.getSkip();
        final int limit = options.getLimit();
        final List<User> r =
            new ArrayList<>(UserServiceTest.this.users.subList(skip, skip + limit));
        return r;
      }
    }).when(q).asList(ArgumentMatchers.any(FindOptions.class));

    final QueryResult<User> returned = this.userService.query(paginationQuery);

    Assert.assertEquals(pageLen, returned.getN());
  }

}
