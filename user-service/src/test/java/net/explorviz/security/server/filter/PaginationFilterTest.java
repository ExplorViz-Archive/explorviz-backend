package net.explorviz.security.server.filter;


import static org.junit.jupiter.api.Assertions.assertEquals; // NOCS
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException; // NOCS
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link PaginationFilter}.
 *
 */
@ExtendWith(MockitoExtension.class)
public class PaginationFilterTest {

  private static final String PAGE_QUERY_PARAM_NAME = "page";
  private static final String PER_PAGE_QUERY_PARAM_NAME = "per_page";

  private static final int NUMBER_OF_ENTITIES = 1000;

  private static final String ERROR_LIST_MESSAGE = "Wrong list was returned.";

  @Mock
  private ContainerRequestContext requestContext;

  @Mock
  private ContainerResponseContext responseContext;

  private PaginationFilter filter;

  private List<Integer> responseList; // NOPMD

  @BeforeEach // NOCS
  public void setUp() {

    this.responseList = new ArrayList<>();
    this.filter = new PaginationFilter();

    final MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    queryParams.putSingle(PAGE_QUERY_PARAM_NAME, "0");
    queryParams.putSingle(PER_PAGE_QUERY_PARAM_NAME, "50");

    for (int i = 0; i < NUMBER_OF_ENTITIES; i++) {
      this.responseList.add(i);
    }

    final UriInfo uriInfo = Mockito.mock(UriInfo.class);

    Mockito.when(this.requestContext.getUriInfo()).thenReturn(uriInfo);
    Mockito.when(uriInfo.getQueryParameters()).thenReturn(queryParams);
    Mockito.when(this.responseContext.getEntity()).thenReturn(this.responseList);
  }

  @Test
  public void testDefaultForNoParams() throws IOException {
    // no query param -> first page, 100 entities -> 0 to 99
    final MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();

    Mockito.when(this.requestContext.getUriInfo().getQueryParameters()).thenReturn(queryParams);

    this.filter.filter(this.requestContext, this.responseContext);

    final List<Integer> expectedList = this.giveListWithIntegers(0, 100);

    assertEquals(expectedList, this.filter.getFinalResultList(), ERROR_LIST_MESSAGE);
  }

  @Test
  public void testPartialPage() throws IOException {
    // query param third page, 30 entities -> 60 to 89
    final MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    queryParams.putSingle(PAGE_QUERY_PARAM_NAME, "3");
    queryParams.putSingle(PER_PAGE_QUERY_PARAM_NAME, "30");

    Mockito.when(this.requestContext.getUriInfo().getQueryParameters()).thenReturn(queryParams);

    this.filter.filter(this.requestContext, this.responseContext);

    final List<Integer> expectedList = this.giveListWithIntegers(60, 30);

    assertEquals(expectedList, this.filter.getFinalResultList(), ERROR_LIST_MESSAGE);
  }

  @Test
  public void testNegativePageQueryParam() throws IOException {
    // query param '-1' page, 30 entities -> BadRequestException
    final MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    queryParams.putSingle(PAGE_QUERY_PARAM_NAME, "-1");
    queryParams.putSingle(PER_PAGE_QUERY_PARAM_NAME, "30");

    Mockito.when(this.requestContext.getUriInfo().getQueryParameters()).thenReturn(queryParams);

    assertThrows(BadRequestException.class,
        () -> this.filter.filter(this.requestContext, this.responseContext));
  }

  @Test
  public void testNonIntegerPageQueryParam() throws IOException {
    // query param 'true' page, 30 entities -> BadRequestException
    final MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    queryParams.putSingle(PAGE_QUERY_PARAM_NAME, "true");
    queryParams.putSingle(PER_PAGE_QUERY_PARAM_NAME, "30");

    Mockito.when(this.requestContext.getUriInfo().getQueryParameters()).thenReturn(queryParams);

    assertThrows(BadRequestException.class,
        () -> this.filter.filter(this.requestContext, this.responseContext));
  }

  @Test
  public void testNonPositivePerPageQueryParam() throws IOException {
    // query param first page, 0 entities -> BadRequestException
    final MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    queryParams.putSingle(PAGE_QUERY_PARAM_NAME, "1");
    queryParams.putSingle(PER_PAGE_QUERY_PARAM_NAME, "0");

    Mockito.when(this.requestContext.getUriInfo().getQueryParameters()).thenReturn(queryParams);

    assertThrows(BadRequestException.class,
        () -> this.filter.filter(this.requestContext, this.responseContext));
  }

  @Test
  public void testNonIntegerPerPageQueryParam() throws IOException {
    // query param first page, 'true' entities -> BadRequestException
    final MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    queryParams.putSingle(PAGE_QUERY_PARAM_NAME, "1");
    queryParams.putSingle(PER_PAGE_QUERY_PARAM_NAME, "true");

    Mockito.when(this.requestContext.getUriInfo().getQueryParameters()).thenReturn(queryParams);

    assertThrows(BadRequestException.class,
        () -> this.filter.filter(this.requestContext, this.responseContext));
  }

  private List<Integer> giveListWithIntegers(final int startInteger,
      final int numberOfSubsequentIntegers) {

    final List<Integer> resultList = new ArrayList<>();

    for (int i = startInteger; i < numberOfSubsequentIntegers + startInteger; i++) {
      resultList.add(i);
    }

    return resultList;
  }

}
