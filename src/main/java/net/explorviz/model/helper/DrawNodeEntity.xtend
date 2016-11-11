package net.explorviz.model.helper;

import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.elk.alg.layered.graph.LNode
import org.eclipse.elk.alg.layered.graph.LGraph
import org.eclipse.elk.alg.layered.graph.LPort
import java.util.Map
import java.util.HashMap

abstract class DrawNodeEntity extends BaseEntity {
	@Accessors String name

	@Accessors transient LGraph kielerGraphReference
	@Accessors transient LNode kielerNodeReference
	@Accessors transient Map<DrawNodeEntity, LPort> sourcePorts = new HashMap<DrawNodeEntity, LPort>()
	@Accessors transient Map<DrawNodeEntity, LPort> targetPorts = new HashMap<DrawNodeEntity, LPort>()
	@Accessors transient float width
	@Accessors transient float height

	@Accessors transient float positionX
	@Accessors transient float positionY
	@Accessors transient float positionZ

}
