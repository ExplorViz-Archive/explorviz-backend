package net.explorviz.model;


import org.eclipse.xtend.lib.annotations.Accessors
import java.util.HashSet
import java.util.Set
import net.explorviz.model.helper.Draw3DNodeEntity

class Clazz extends Draw3DNodeEntity {
	@Accessors var int instanceCount = 0
	@Accessors val transient Set<Integer> objectIds = new HashSet<Integer>()

	@Accessors Component parent
	@Accessors var boolean visible = false


	def void clearAllPrimitiveObjects() {
//		this.primitiveObjects.clear()
	}

	override void highlight() {
//		for (primitiveObject : this.primitiveObjects)
//			primitiveObject.highlight(ColorDefinitions::highlightColor)
//
//		highlighted = true
	}

	override unhighlight() {
//		if (highlighted) {
//			for (primitiveObject : this.primitiveObjects)
//				primitiveObject.unhighlight()
//				
//			highlighted = false
//		}
	}

}
