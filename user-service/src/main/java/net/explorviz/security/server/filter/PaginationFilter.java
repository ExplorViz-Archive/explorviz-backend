package net.explorviz.security.server.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * This filter checks a request for pagination query parameters "page" and "per_page" and filters a
 * potential response with a list based on these query parameters.
 */
public class PaginationFilter implements ContainerResponseFilter {

  private static final String PAGE_QUERY_PARAM_NAME = "page";
  private static final String PER_PAGE_QUERY_PARAM_NAME = "per_page";

  private static final int DEFAULT_PAGE_VALUE = 0;
  private static final int DEFAULT_PER_PAGE_VALUE = 100;

  @Override
  public void filter(final ContainerRequestContext requestContext,
      final ContainerResponseContext responseContext) throws IOException {

    final Object entity = responseContext.getEntity();

    // only proceed if response entity is of type list
    if (entity != null && entity instanceof List) {

      @SuppressWarnings("unchecked")
      final List<Object> entityList = (List<Object>) entity;

      final MultivaluedMap<String, String> queryParams =
          requestContext.getUriInfo().getQueryParameters();

      final String pageParam = queryParams.getFirst(PAGE_QUERY_PARAM_NAME);
      int pageParamValue = DEFAULT_PAGE_VALUE;

      if (pageParam != null) {
        pageParamValue = Integer.valueOf(pageParam);
      }

      final String perPageParam = queryParams.getFirst(PER_PAGE_QUERY_PARAM_NAME);
      int perPageParamValue = DEFAULT_PER_PAGE_VALUE;

      if (perPageParam != null) {
        perPageParamValue = Integer.valueOf(perPageParam);
      }

      final List<List<Object>> resultList = this.splitListIntoChunks(entityList, perPageParamValue);

      if (pageParamValue <= resultList.size()) {
        responseContext.setEntity(resultList.get(pageParamValue));
      } else {
        responseContext.setEntity(resultList.get(0));
      }
    }
  }

  private List<List<Object>> splitListIntoChunks(final List<Object> listToSplit,
      final int chunkSize) {
    final AtomicInteger counter = new AtomicInteger();
    final List<List<Object>> resultList = new ArrayList<>();

    for (final Object o : listToSplit) {
      if (counter.getAndIncrement() % chunkSize == 0) {
        resultList.add(new ArrayList<>()); // NOPMD
      }
      resultList.get(resultList.size() - 1).add(o);
    }
    return resultList;
  }

}
