package net.explorviz.security.server.filter;


import static org.junit.Assert.assertEquals; // NOCS
import java.util.ArrayList; // NOCS
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import net.explorviz.security.server.resources.UserResource;
import net.explorviz.shared.security.model.User;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Integration tests for {@link PaginationFilter}.
 *
 */
public class PaginationFilterIntegrationTest extends JerseyTest {

  private static final String BASE_URL = "v1/users";
  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private static final int DEFAULT_PER_PAGE_VALUE = 100;

  private static final String ROLE_QUERY_PARAM = "role";
  private static final String TEST_ROLE = "testRole";

  private static final String PAGE_QUERY_PARAM_NAME = "page";
  private static final String PER_PAGE_QUERY_PARAM_NAME = "per_page";

  private static final int NUMBER_OF_ENTITIES = 1000;

  private static final String ERROR_LIST_MESSAGE = "Wrong list was returned.";

  @Override
  protected Application configure() {

    final List<User> responseList = new ArrayList<>();
    for (int i = 0; i < NUMBER_OF_ENTITIES; i++) {
      responseList.add(new User(String.valueOf(i))); // NOPMD
    }

    final UserResource u = Mockito.mock(UserResource.class);

    Mockito.when(u.usersByRole(TEST_ROLE)).thenReturn(responseList);

    return new ResourceConfig().register(PaginationFilter.class).register(u);
  }

  @Test
  public void testDefaultForNoParams() { // NOPMD
    final Response response = this.target()
        .path(BASE_URL)
        .queryParam(ROLE_QUERY_PARAM, TEST_ROLE)
        .request()
        .accept(MEDIA_TYPE)
        .get();

    final List<User> responseEntity = response.readEntity(new GenericType<List<User>>() {});

    assertEquals(ERROR_LIST_MESSAGE, DEFAULT_PER_PAGE_VALUE, responseEntity.size());
  }

  @Test
  public void testPartialPage() { // NOPMD
    final Response response = this.target()
        .path(BASE_URL)
        .queryParam(ROLE_QUERY_PARAM, TEST_ROLE)
        .queryParam(PAGE_QUERY_PARAM_NAME, "3")
        .queryParam(PER_PAGE_QUERY_PARAM_NAME, "5")
        .request()
        .accept(MEDIA_TYPE)
        .get();

    final List<User> responseEntity = response.readEntity(new GenericType<List<User>>() {});

    final List<User> expectedList = new ArrayList<>();
    expectedList.add(new User("10"));
    expectedList.add(new User("11"));
    expectedList.add(new User("12"));
    expectedList.add(new User("13"));
    expectedList.add(new User("14"));

    final List<String> responseEntityUsernames = this.getUsernameListFromUserList(responseEntity);
    final List<String> expectedListUsernames = this.getUsernameListFromUserList(expectedList);

    assertEquals(ERROR_LIST_MESSAGE, expectedListUsernames, responseEntityUsernames); // NOCS
  }


  private List<String> getUsernameListFromUserList(final List<User> l1) {
    return l1.stream().map(User::getUsername).collect(Collectors.toList());
  }

}
