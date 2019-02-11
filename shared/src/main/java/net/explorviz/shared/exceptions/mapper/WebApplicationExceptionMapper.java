package net.explorviz.shared.exceptions.mapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import net.explorviz.shared.exceptions.ErrorObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top-level exception mapper that prevents exception bleeding to the outside world.
 * {@link WebApplicationException} and its sub classes are catched and transferred to a JSON-API
 * compliant error object.
 */
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

  @Context
  private ErrorObjectHelper errorObjectHelper;

  @Override
  public Response toResponse(final WebApplicationException exception) {

    final int httpStatus = exception.getResponse().getStatus();

    LOGGER.error("Error occured: HTTP Status={}", httpStatus, exception);

    final String errorString =
        errorObjectHelper.createErrorObjectString(httpStatus, "Error", exception.getMessage());

    return Response.status(httpStatus).header("Content-Type", "application/json")
        .entity(errorString).build();
  }
}
