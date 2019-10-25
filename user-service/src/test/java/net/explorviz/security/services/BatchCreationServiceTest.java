package net.explorviz.security.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.explorviz.security.model.UserBatchRequest;
import net.explorviz.security.services.exceptions.UserCrudException;
import net.explorviz.security.user.User;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.security.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

/**
 * Unit test for {@link BatchService}.
 *
 */
@ExtendWith(MockitoExtension.class)
public class BatchCreationServiceTest {

  @Mock
  private UserService us;

  @Mock
  private IdGenerator idGenerator;

  private final AtomicInteger id = new AtomicInteger(0);

  private UserService userService;

  private BatchService bcs;

  private final List<User> users = new ArrayList<>();

  @BeforeEach
  public void setUp() {
    this.bcs = new BatchService(this.us, null, this.idGenerator, "", "");
  }

  @Test
  public void testCreateAll() throws UserCrudException {
    final int size = 3;
    final List<String> passwords = Arrays.asList("abc", "abc", "bac");


    final UserBatchRequest batch =
        new UserBatchRequest("test", size, passwords, Arrays.asList(Role.ADMIN_NAME), null);

    Mockito.doAnswer(new Answer<User>() {

      @Override
      public User answer(final InvocationOnMock invocation) throws Throwable {
        final User u = invocation.getArgument(0);
        BatchCreationServiceTest.this.users.add(u);
        return u;
      }

    }).when(this.us).saveNewEntity(ArgumentMatchers.any(User.class));



    Mockito.when(this.idGenerator.generateId())
        .thenReturn(Long.toString(this.id.incrementAndGet()));

    this.bcs.create(batch, "");

    assertEquals(size, this.users.size());

  }


}
