package net.explorviz.security.server.filter;


import static org.junit.Assert.assertEquals;
import com.github.jasminb.jsonapi.ResourceConverter;
import java.util.ArrayList; // NOCS
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import net.explorviz.security.server.providers.GenericJsonApiPaginationWriter;
import net.explorviz.security.server.resources.UserResource;
import net.explorviz.shared.security.model.User;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Integration tests for {@link GenericJsonApiPaginationWriter}.
 *
 */
public class GenericJsonApiPaginationWriterIntegrationTest extends JerseyTest {

  private static final String BASE_URL = "v1/users";
  private static final String MEDIA_TYPE = "application/vnd.api+json";

  private static final String ROLE_QUERY_PARAM = "role";
  private static final String TEST_ROLE = "testRole";

  private static final String PAGE_QUERY_PARAM_NAME = "page";
  private static final String PER_PAGE_QUERY_PARAM_NAME = "per_page";

  private static final String ERROR_LIST_MESSAGE = "Wrong list was returned.";

  private ResourceConverter converter;

  private UriInfo uriInfo;

  @Override
  protected Application configure() {

    final List<User> responseList = this.giveUserList(0, 1000);

    final UserResource u = Mockito.mock(UserResource.class);

    Mockito.when(u.usersByRole(TEST_ROLE)).thenReturn(responseList);

    this.converter = new ResourceConverter(User.class);

    this.uriInfo = Mockito.mock(UriInfo.class);

    return new ResourceConfig()
        .register(new GenericJsonApiPaginationWriter(this.converter, this.uriInfo))
        .register(u);
  }

  @Test
  public void testDefaultForNoParams() {

    this.setQueryParameter(false, null, null);

    final Response response = this.target()
        .path(BASE_URL)
        .queryParam(ROLE_QUERY_PARAM, TEST_ROLE)
        .request()
        .accept(MEDIA_TYPE)
        .get();

    final List<User> responseList =
        this.converter.readDocumentCollection(response.readEntity(byte[].class), User.class).get();

    final List<String> responseEntityUsernames = this.getUsernameListFromUserList(responseList);
    final List<String> expectedListUsernames =
        this.getUsernameListFromUserList(this.giveUserList(0, 1000));

    assertEquals(ERROR_LIST_MESSAGE, expectedListUsernames, responseEntityUsernames);
  }

  @Test
  public void testPartialPage() {

    this.setQueryParameter(true, "3", "5");

    final Response response = this.target()
        .path(BASE_URL)
        .queryParam(ROLE_QUERY_PARAM, TEST_ROLE)
        .queryParam(PAGE_QUERY_PARAM_NAME, "3")
        .queryParam(PER_PAGE_QUERY_PARAM_NAME, "5")
        .request()
        .accept(MEDIA_TYPE)
        .get();

    final List<User> responseList =
        this.converter.readDocumentCollection(response.readEntity(byte[].class), User.class).get();

    final List<String> responseEntityUsernames = this.getUsernameListFromUserList(responseList);
    final List<String> expectedListUsernames =
        this.getUsernameListFromUserList(this.giveUserList(10, 5));

    assertEquals(ERROR_LIST_MESSAGE, expectedListUsernames, responseEntityUsernames);
  }

  // TODO check links attribute of JSONAPI Document

  private void setQueryParameter(final boolean useQueryParams, final String pageQueryValue,
      final String perPageQueryValue) {
    final MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();

    if (useQueryParams) {
      queryParams.putSingle(PAGE_QUERY_PARAM_NAME, pageQueryValue);
      queryParams.putSingle(PER_PAGE_QUERY_PARAM_NAME, perPageQueryValue);
    }

    Mockito.when(this.uriInfo.getQueryParameters()).thenReturn(queryParams);
  }

  private List<String> getUsernameListFromUserList(final List<User> l1) {
    return l1.stream().map(User::getUsername).collect(Collectors.toList());
  }

  private List<User> giveUserList(final int startInteger, final int numberOfSubsequentIntegers) {

    final List<User> resultList = new ArrayList<>();

    for (int i = startInteger; i < numberOfSubsequentIntegers + startInteger; i++) {
      resultList.add(new User(String.valueOf(i), String.valueOf(i), null, null)); // NOPMD
    }

    return resultList;
  }

}
