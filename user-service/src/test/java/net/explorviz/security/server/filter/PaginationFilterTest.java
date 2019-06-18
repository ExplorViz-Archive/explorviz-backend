package net.explorviz.security.server.filter;


import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;// NOCS
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link PaginationFilter}.
 *
 */
@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@SuppressWarnings("PMD")
public class PaginationFilterTest {

  private static final String PAGE_QUERY_PARAM_NAME = "page";
  private static final String PER_PAGE_QUERY_PARAM_NAME = "per_page";

  private static final int NUMBER_OF_ENTITIES = 1000;

  @Mock
  private ContainerRequestContext requestContext;

  @Mock
  private ContainerResponseContext responseContext;

  private PaginationFilter filter;

  private List<Integer> responseList;

  @BeforeEach
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
  public void testPartialPage() throws IOException {
    // first page, 30 entities
    // filter does not return
    final MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    queryParams.putSingle(PAGE_QUERY_PARAM_NAME, "3");
    queryParams.putSingle(PER_PAGE_QUERY_PARAM_NAME, "30");

    Mockito.when(this.requestContext.getUriInfo().getQueryParameters()).thenReturn(queryParams);

    this.filter.filter(this.requestContext, this.responseContext);

    final List<Integer> expectedList = this.giveListWithIntegers(60, 30);

    assertEquals(expectedList, this.filter.getFinalResultList());
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
