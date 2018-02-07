package net.explorviz.model.helper;

import org.eclipse.xtend.lib.annotations.Accessors
import java.util.List
import java.util.ArrayList
import com.github.jasminb.jsonapi.annotations.Relationship

import com.github.jasminb.jsonapi.annotations.Type
import net.explorviz.model.communication.ApplicationCommunication

@Type("communicationtileaccumulator")
class CommunicationTileAccumulator extends DrawEdgeEntity {
	@Accessors int requestsCache
	
	@Accessors val transient List<ApplicationCommunication> communications = new ArrayList<ApplicationCommunication>(4)
	@Accessors transient boolean alreadyDrawn = false
	 
	@Accessors Point startPoint 
	@Accessors Point endPoint 
	
	@Relationship("parent")
	@Accessors CommunicationAccumulator parent

}
