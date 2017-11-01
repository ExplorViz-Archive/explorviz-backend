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

	@Relationship("target")
	@Accessors Application target

	@Relationship("sourceClazz")
	@Accessors Clazz sourceClazz

	@Relationship("targetClazz")
	@Accessors Clazz targetClazz

}
