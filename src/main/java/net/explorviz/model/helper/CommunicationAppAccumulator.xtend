package net.explorviz.model.helper;

import java.util.ArrayList
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import com.github.jasminb.jsonapi.annotations.Type
import net.explorviz.model.communication.ClazzCommunication

@Type("communicationappaccumulator")
class CommunicationAppAccumulator extends Draw3DEdgeEntity {
	
	@Accessors Draw3DNodeEntity source
	
	@Accessors Draw3DNodeEntity target
	
	@Accessors int requests
	@Accessors float averageResponseTime	
	
	@Accessors val transient List<ClazzCommunication> aggregatedCommunications = new ArrayList<ClazzCommunication> 
	
}