package net.explorviz.model.helper;

import java.util.ArrayList
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.CommunicationClazz
import com.github.jasminb.jsonapi.annotations.Type

@Type("communicationappaccumulator")
class CommunicationAppAccumulator extends Draw3DEdgeEntity {
	
	@Accessors Draw3DNodeEntity source
	
	@Accessors Draw3DNodeEntity target
	
	@Accessors int requests
	@Accessors float averageResponseTime	
	
	@Accessors val transient List<CommunicationClazz> aggregatedCommunications = new ArrayList<CommunicationClazz> 
	
}