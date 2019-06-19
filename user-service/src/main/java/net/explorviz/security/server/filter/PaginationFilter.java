package net.explorviz.security.server.filter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.BadRequestException;
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

  private static final String QUERY_EXCEPTION_MESSAGE =
      "Query parameter '{0}' has to be a non-negative integer value.";

  private static final int MIN_PAGE_VALUE = 1;
  private static final int MIN_PER_PAGE_VALUE = 1;

  private static final int PAGE_CALC_OFFSET = 1;

  private static final int DEFAULT_PAGE_VALUE = 1;
  private static final int DEFAULT_PER_PAGE_VALUE = 100;

  private List<Object> finalResultList = new ArrayList<>();

  @Override
  public void filter(final ContainerRequestContext requestContext, // NOPMD
      final ContainerResponseContext responseContext) throws IOException {

    final Object entity = responseContext.getEntity();

    // only proceed if response entity is of type list
    if (!(entity instanceof List)) {
      return;
    }

    final MultivaluedMap<String, String> queryParams =
        requestContext.getUriInfo().getQueryParameters();

    // Get potential pagination page
    final String pageParam = queryParams.getFirst(PAGE_QUERY_PARAM_NAME);
    int pageParamValue = DEFAULT_PAGE_VALUE;

    if (pageParam != null) {
      try {
        pageParamValue = Integer.valueOf(pageParam);

        if (pageParamValue < MIN_PAGE_VALUE) { // NOPMD
          throw new NumberFormatException();
        }
      } catch (final NumberFormatException e) {
        final String exceptionMessage =
            MessageFormat.format(QUERY_EXCEPTION_MESSAGE, PAGE_QUERY_PARAM_NAME);
        throw new BadRequestException(exceptionMessage, e);
      }
    }

    // Get potential pagination per_page
    final String perPageParam = queryParams.getFirst(PER_PAGE_QUERY_PARAM_NAME);
    int perPageParamValue = DEFAULT_PER_PAGE_VALUE;

    if (perPageParam != null) {
      try {
        perPageParamValue = Integer.valueOf(perPageParam);

        if (perPageParamValue < MIN_PER_PAGE_VALUE) { // NOPMD
          throw new NumberFormatException();
        }
      } catch (final NumberFormatException e) {
        final String exceptionMessage =
            MessageFormat.format(QUERY_EXCEPTION_MESSAGE, PER_PAGE_QUERY_PARAM_NAME);
        throw new BadRequestException(exceptionMessage, e);
      }
    }

    // build resultList
    @SuppressWarnings("unchecked")
    final List<Object> entityList = (List<Object>) entity;

    final List<List<Object>> resultList = this.splitListIntoChunks(entityList, perPageParamValue);

    final int pageToListIndex = pageParamValue - PAGE_CALC_OFFSET;

    if (pageToListIndex <= resultList.size()) {
      this.finalResultList = resultList.get(pageToListIndex);
    } else {
      // page number is higher than actual page maximum -> return no entities
      this.finalResultList = new ArrayList<>();
    }

    // finally modify response entity
    responseContext.setEntity(this.finalResultList);
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

  public List<Object> getFinalResultList() {
    return this.finalResultList;
  }

}
