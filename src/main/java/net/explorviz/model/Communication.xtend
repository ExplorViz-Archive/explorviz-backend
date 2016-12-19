package net.explorviz.model;

import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.helper.DrawEdgeEntity
import com.github.jasminb.jsonapi.annotations.Type
import com.github.jasminb.jsonapi.annotations.Relationship

@Type("communication")
class Communication extends DrawEdgeEntity {
	@Accessors int requests
	@Accessors String technology

	@Accessors float averageResponseTimeInNanoSec

	@Relationship("source")
	@Accessors Application source
	
	@Relationship("source")
	@Accessors Application target

	@Relationship("source")
	@Accessors Clazz sourceClazz
	
	@Relationship("source")
	@Accessors Clazz targetClazz
	
	new(String id) {
		this.id = id
	}

}
