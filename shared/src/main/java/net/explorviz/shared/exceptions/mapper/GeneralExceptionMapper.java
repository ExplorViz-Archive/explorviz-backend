package net.explorviz.shared.exceptions.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Final top-level generic exception mapper that prevents exception bleeding to the outside world.
 * {@link Throwable} and its sub classes are catched and transferred to a JSON-API compliant error
 * object.
 */
public class GeneralExceptionMapper implements ExceptionMapper<Throwable> {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String MEDIA_TYPE = "application/json";

  private static final int HTTP_ERROR_CODE = 500;
  private static final int HTTP_NOT_FOUND_CODE = 404;

  @Override
  public Response toResponse(final Throwable exception) {

    Response.ResponseBuilder response =
        Response.status(HTTP_ERROR_CODE).header(CONTENT_TYPE, MEDIA_TYPE);

    String message = "Unknown Server Error";

    if (exception instanceof NotFoundException) {
      response = response.status(HTTP_NOT_FOUND_CODE);
      message = "404 - not found";
    } else {
      System.err.println(exception);
    }

    final Map<String, Object> error = new HashMap<>();

    error.put("message", exception.getMessage());
    error.put("exception", exception.toString());

    final Map<String, Object> jsonDummy = new HashMap<>();
    jsonDummy.put("error", error); // easier to spot in JS which check on error property

    final ObjectMapper mapper = new ObjectMapper();
    try {
      message = mapper.writeValueAsString(jsonDummy);
    } catch (final JsonProcessingException e) {
      System.err.println(e);
    }
    return Response.status(HTTP_ERROR_CODE).header(CONTENT_TYPE, MEDIA_TYPE).entity(message)
        .build();
  }
}
