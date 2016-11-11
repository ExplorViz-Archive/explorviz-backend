package net.explorviz.server.main;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mathis Neumann <mne@informatik.uni-kiel.de>
 */
public class GeneralExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {

        Response.ResponseBuilder response = Response.status(500)
                .header("Content-Type", "application/json");

        String message = "Unknown Server Error";

        if(exception instanceof NotFoundException) {
            response = response.status(404);
            message = "404 - not found";
        } else {
            exception.printStackTrace();
        }

        Map<String, Object> error = new HashMap<>();

        error.put("message", exception.getMessage());
        error.put("exception", exception.toString());

        Map<String, Object> jsonDummy = new HashMap<>();
        jsonDummy.put("error", error); // easier to spot in JS which check on error property

        ObjectMapper mapper = new ObjectMapper();
        try {
            message = mapper.writeValueAsString(jsonDummy);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Response
                .status(500)
                .header("Content-Type", "application/json")
                .entity(message)
                .build();
    }
}
