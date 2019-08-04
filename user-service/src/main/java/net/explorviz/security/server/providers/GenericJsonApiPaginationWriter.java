package net.explorviz.security.server.providers;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.Link;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * {@link MessageBodyWriter} implementation for generic lists and pagination support.
 *
 */
public class GenericJsonApiPaginationWriter implements MessageBodyWriter<List<?>> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GenericJsonApiPaginationWriter.class);

  // Pagination constants
  private static final String PAGE_QUERY_PARAM_NAME = "page";
  private static final String PER_PAGE_QUERY_PARAM_NAME = "per_page";

  private static final String QUERY_EXCEPTION_MESSAGE =
      "Query parameter '{0}' has to be a non-negative integer value.";

  private static final int MIN_PAGE_VALUE = 1;
  private static final int MIN_PER_PAGE_VALUE = 1;

  private static final int PAGE_CALC_OFFSET = 1;

  private static final int DEFAULT_PAGE_VALUE = 1;
  private static final int DEFAULT_PER_PAGE_VALUE = 100;

  // only for testing purposes
  private List<?> finalResultList = new ArrayList<>();

  // Instance attributes
  private final ResourceConverter converter;
  private final UriInfo uriInfo;

  @Inject
  public GenericJsonApiPaginationWriter(final ResourceConverter converter,
      @Context final UriInfo uriInfo) {
    this.converter = converter;
    this.uriInfo = uriInfo;
  }

  @Override
  public boolean isWriteable(final Class<?> type, final Type genericType,
      final Annotation[] annotations, final MediaType mediaType) {
    return true;
  }

  @Override
  public void writeTo(final List<?> t, final Class<?> type, final Type genericType,
      final Annotation[] annotations, final MediaType mediaType,
      final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream)
      throws IOException, WebApplicationException {


    JSONAPIDocument<List<?>> document = new JSONAPIDocument<>(t);

    if (this.uriInfo.getQueryParameters().containsKey(PER_PAGE_QUERY_PARAM_NAME)
        || this.uriInfo.getQueryParameters().containsKey(PAGE_QUERY_PARAM_NAME)) {
      document = this.getJsonApiPaginationDoc(t);
    } else {
      // for testing
      this.finalResultList = t;
    }

    try { // NOPMD
      entityStream.write(this.converter.writeDocumentCollection(document));
    } catch (final DocumentSerializationException e) {
      LOGGER.error("Error when serializing list to JSON API: ", e);
    } finally {
      entityStream.flush();
      entityStream.close();
    }
  }

  public List<?> getFinalResultList() {
    return this.finalResultList;
  }

  private JSONAPIDocument<List<?>> getJsonApiPaginationDoc(final List<?> unprocessedList) { // NOPMD

    final MultivaluedMap<String, String> queryParams = this.uriInfo.getQueryParameters();

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

    // List of lists List<List<?>>
    final List<List<Object>> resultList =
        this.splitListIntoChunks(unprocessedList, perPageParamValue);

    final int pageToListIndex = pageParamValue - PAGE_CALC_OFFSET;

    if (pageToListIndex <= resultList.size()) {
      this.finalResultList = resultList.get(pageToListIndex);
    } else {
      // page number is higher than actual page maximum -> return no entities
      this.finalResultList = new ArrayList<>();
    }

    final JSONAPIDocument<List<?>> resultDocument = new JSONAPIDocument<>(this.finalResultList);

    // set JSON-API links for pagination
    // https://jsonapi.org/format/#fetching-pagination

    resultDocument.addLink("first", new Link(""));
    resultDocument.addLink("last", new Link(""));
    resultDocument.addLink("prev", new Link(""));
    resultDocument.addLink("next", new Link(""));

    return resultDocument;
  }

  private List<List<Object>> splitListIntoChunks(final List<?> listToSplit, final int chunkSize) {
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
