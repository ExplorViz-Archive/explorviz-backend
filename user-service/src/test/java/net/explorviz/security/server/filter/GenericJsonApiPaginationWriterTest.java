package net.explorviz.security.server.filter;


import static org.junit.jupiter.api.Assertions.assertEquals; // NOCS
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.github.jasminb.jsonapi.ResourceConverter; // NOCS
import java.io.IOException; // NOCS
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import net.explorviz.security.server.providers.GenericJsonApiPaginationWriter;
import net.explorviz.security.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link PaginationFilter}.
 *
 */
@ExtendWith(MockitoExtension.class)
public class GenericJsonApiPaginationWriterTest {

  private static final String PAGE_QUERY_PARAM_NAME = "page";
  private static final String PER_PAGE_QUERY_PARAM_NAME = "per_page";

  private static final int NUMBER_OF_ENTITIES = 1000;

  private static final String ERROR_LIST_MESSAGE = "Wrong list was returned.";

  private GenericJsonApiPaginationWriter writer;

  private List<User> responseList; // NOPMD

  private OutputStream entityStream;


  private void manualSetUp(final boolean useQueryParams, final String pageQueryValue,
      final String perPageQueryValue) {

    this.entityStream = Mockito.mock(OutputStream.class);

    this.responseList = new ArrayList<>();

    final UriInfo uriInfo = Mockito.mock(UriInfo.class);

    final MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();

    if (useQueryParams) {
      queryParams.putSingle(PAGE_QUERY_PARAM_NAME, pageQueryValue);
      queryParams.putSingle(PER_PAGE_QUERY_PARAM_NAME, perPageQueryValue);
    }

    Mockito.when(uriInfo.getQueryParameters()).thenReturn(queryParams);

    final ResourceConverter converter = new ResourceConverter(User.class);
    this.writer = new GenericJsonApiPaginationWriter(converter, uriInfo);

    for (Integer i = 0; i < NUMBER_OF_ENTITIES; i++) {
      this.responseList.add(new User(String.valueOf(i))); // NOPMD
    }
  }

  @Test
  public void testDefaultForNoParams() throws IOException {
    // no query param -> all entities
    this.manualSetUp(false, null, null);

    this.writer.writeTo(this.responseList, null, null, null, null, null, this.entityStream);

    final List<User> expectedList = this.giveUserList(0, 1000);

    final List<String> responseEntityUsernames =
        this.getUsernameListFromUserList(castList(User.class, this.writer.getFinalResultList()));
    final List<String> expectedListUsernames = this.getUsernameListFromUserList(expectedList);

    assertEquals(expectedListUsernames, responseEntityUsernames, ERROR_LIST_MESSAGE);
  }

  @Test
  public void testPartialPage() throws IOException {
    // query param third page, 30 entities -> 60 to 89
    this.manualSetUp(true, "3", "30");

    this.writer.writeTo(this.responseList, null, null, null, null, null, this.entityStream);

    final List<User> expectedList = this.giveUserList(60, 30);

    final List<String> responseEntityUsernames =
        this.getUsernameListFromUserList(castList(User.class, this.writer.getFinalResultList()));
    final List<String> expectedListUsernames = this.getUsernameListFromUserList(expectedList);

    assertEquals(expectedListUsernames, responseEntityUsernames, ERROR_LIST_MESSAGE);
  }

  @Test
  public void testNegativePageQueryParam() throws IOException {
    // query param '-1' page, 30 entities -> BadRequestException
    this.manualSetUp(true, "-1", "30");

    assertThrows(BadRequestException.class,
        () -> this.writer
            .writeTo(this.responseList, null, null, null, null, null, this.entityStream));
  }

  @Test
  public void testNonIntegerPageQueryParam() throws IOException {
    // query param 'true' page, 30 entities -> BadRequestException
    this.manualSetUp(true, "true", "30"); // NOCS

    assertThrows(BadRequestException.class,
        () -> this.writer
            .writeTo(this.responseList, null, null, null, null, null, this.entityStream));
  }

  @Test
  public void testNonPositivePerPageQueryParam() throws IOException {
    // query param first page, 0 entities -> BadRequestException
    this.manualSetUp(true, "1", "0");

    assertThrows(BadRequestException.class,
        () -> this.writer
            .writeTo(this.responseList, null, null, null, null, null, this.entityStream));
  }

  @Test
  public void testNonIntegerPerPageQueryParam() throws IOException {
    // query param first page, 'true' entities -> BadRequestException
    this.manualSetUp(true, "1", "true");

    assertThrows(BadRequestException.class,
        () -> this.writer
            .writeTo(this.responseList, null, null, null, null, null, this.entityStream));
  }

  private List<User> giveUserList(final int startInteger, final int numberOfSubsequentIntegers) {

    final List<User> resultList = new ArrayList<>();

    for (int i = startInteger; i < numberOfSubsequentIntegers + startInteger; i++) {
      resultList.add(new User(String.valueOf(i))); // NOPMD
    }

    return resultList;
  }

  private List<String> getUsernameListFromUserList(final List<User> l1) {
    return l1.stream().map(User::getUsername).collect(Collectors.toList());
  }

  private static <T> List<T> castList(final Class<T> clazz, final List<?> items) {
    return items.stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
  }

}
