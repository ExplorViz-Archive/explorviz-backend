package net.explorviz.shared.exceptions.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Final top-level generic exception mapper that prevents exception bleeding to the outside world.
 * {@link Throwable} and its sub classes are catched and transferred to a JSON-API compliant error
 * object.
 */
public class GeneralExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralExceptionMapper.class);

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String MEDIA_TYPE = "application/json";

  private static final int HTTP_ERROR_CODE = 500;
  private static final int HTTP_NOT_FOUND_CODE = 404;

  @Override
  public Response toResponse(final Throwable exception) {

    Response.ResponseBuilder response = // NOPMD
        Response.status(HTTP_ERROR_CODE).header(CONTENT_TYPE, MEDIA_TYPE);

    String message = "Unknown Server Error"; // NOPMD

    if (exception instanceof NotFoundException) {
      response = response.status(HTTP_NOT_FOUND_CODE);
      message = "404 - not found"; // NOPMD
    } else {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error("General error occured", exception);
      }
    }

    final Map<String, Object> error = new HashMap<>();

    error.put("message", exception.getMessage());
    error.put("exception", exception.toString());

    final Map<String, Object> jsonDummy = new HashMap<>();
    jsonDummy.put("error", error); // easier to spot in JS which check on error property

    final ObjectMapper mapper = new ObjectMapper();
    try {
      message = mapper.writeValueAsString(jsonDummy); // NOPMD
    } catch (final JsonProcessingException e) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error(e.toString());
      }
    }
    return Response.status(HTTP_ERROR_CODE).header(CONTENT_TYPE, MEDIA_TYPE).entity(message)
        .build();
  }
}
