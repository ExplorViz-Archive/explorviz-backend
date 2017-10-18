package net.explorviz.server.exceptions.mapper

import javax.ws.rs.ext.ExceptionMapper
import org.glassfish.jersey.server.ParamException.PathParamException
import javax.ws.rs.core.Response
import java.util.HashMap
import java.util.Map
import java.util.ArrayList
import java.util.List
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.JsonProcessingException

class PathParamExceptionMapper implements ExceptionMapper<PathParamException> {

	override toResponse(PathParamException exception) {
		
		var httpErrorCode = 400
		        
        var List<Map<String, Object>> array = new ArrayList<Map<String, Object>>		
        
        var Map<String, Object> errorObject = new HashMap<String, Object>()
        errorObject.put("status", httpErrorCode.toString)
        errorObject.put("title", "Invalid path parameter(s)")
        errorObject.put("detail", exception.cause.toString)

		array.add(errorObject)
		
		var Map<String, Object> errorsArray = new HashMap<String, Object>()
        errorsArray.put("errors", array.toArray)        
        
        
        var String returnMessage        
        
        var mapper = new ObjectMapper()
        try {
            returnMessage = mapper.writeValueAsString(errorsArray);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
		

		return Response.status(httpErrorCode).header("Content-Type", "application/json").entity(returnMessage).build();

	}

}
