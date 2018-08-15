package net.explorviz.server.exceptions.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.glassfish.jersey.server.ParamException.QueryParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Exeception mapping for query parameter exceptions
 *
 */
public class QueryParamExceptionMapper implements ExceptionMapper<QueryParamException> {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryParamExceptionMapper.class);

	@Override
	public Response toResponse(final QueryParamException exception) {

		final int httpErrorCode = 400;

		final List<Map<String, Object>> array = new ArrayList<Map<String, Object>>();

		final Map<String, Object> errorObject = new HashMap<String, Object>();
		errorObject.put("status", String.valueOf(httpErrorCode));
		errorObject.put("title", "Invalid path parameter(s)");
		errorObject.put("detail", exception.getCause().toString());

		array.add(errorObject);

		final Map<String, Object> errorsArray = new HashMap<String, Object>();
		errorsArray.put("errors", array.toArray());

		String returnMessage = "";

		final ObjectMapper mapper = new ObjectMapper();

		try {
			returnMessage = mapper.writeValueAsString(errorsArray);
		} catch (final JsonProcessingException e) {
			LOGGER.debug(e.getMessage());
		}

		return Response.status(httpErrorCode).header("Content-Type", "application/json").entity(returnMessage).build();

	}

}
