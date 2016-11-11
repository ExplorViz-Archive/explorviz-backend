package net.explorviz.model;

import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.helper.DrawEdgeEntity

class Communication extends DrawEdgeEntity {
	@Accessors int requests
	@Accessors String technology

	@Accessors float averageResponseTimeInNanoSec

	@Accessors Application source
	@Accessors Application target

	@Accessors Clazz sourceClazz
	@Accessors Clazz targetClazz

}
