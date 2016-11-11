package net.explorviz.model.helper;

//import de.cau.cs.kieler.klay.layered.graph.LGraph
//import de.cau.cs.kieler.klay.layered.graph.LNode
//import de.cau.cs.kieler.klay.layered.graph.LPort
//import java.util.HashMap
//import java.util.Map
import org.eclipse.xtend.lib.annotations.Accessors

abstract class DrawNodeEntity extends BaseEntity {
	@Accessors String name

	// @Accessors transient LGraph kielerGraphReference
	// @Accessors transient LNode kielerNodeReference
	// @Accessors transient Map<DrawNodeEntity, LPort> sourcePorts = new HashMap<DrawNodeEntity, LPort>()
	// @Accessors transient Map<DrawNodeEntity, LPort> targetPorts = new HashMap<DrawNodeEntity, LPort>()
	@Accessors transient float width
	@Accessors transient float height

	@Accessors transient float positionX
	@Accessors transient float positionY
	@Accessors transient float positionZ

}
