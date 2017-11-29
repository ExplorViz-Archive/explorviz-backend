package net.explorviz.model;

import java.util.ArrayList
import org.eclipse.xtend.lib.annotations.Accessors
import java.util.List
import net.explorviz.model.helper.Draw3DNodeEntity
import com.github.jasminb.jsonapi.annotations.Type
import com.github.jasminb.jsonapi.annotations.Relationship
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@Type("component")
@JsonIgnoreProperties("belongingApplication")
class Component extends Draw3DNodeEntity {
	@Accessors var String name
	@Accessors var String fullQualifiedName
	@Accessors var boolean synthetic = false
	@Accessors var boolean foundation = false

	@Relationship("children")
	@Accessors var List<Component> children = new ArrayList<Component>

	@Relationship("clazzes")
	@Accessors var List<Clazz> clazzes = new ArrayList<Clazz>

	@Relationship("parentComponent")
	@Accessors Component parentComponent

	//@Relationship("belongingApplication")
	// Don't parse since cycle results in stackoverflow when accessing latestLandscape
	@Accessors Application belongingApplication

	var boolean opened = false

	def boolean isOpened() {
		opened
	}

	def void setOpened(boolean openedParam) {
		if(!openedParam) setAllChildrenUnopened()

		this.opened = openedParam
	}

	private def setAllChildrenUnopened() {
		for (child : children)
			child.setOpened(false)
	}

	def void openAllComponents() {
		opened = true
		for (child : children)
			child.openAllComponents()
	}

	def void closeAllComponents() {
		opened = false
		for (child : children)
			child.closeAllComponents()
	}

	def void clearAllPrimitiveObjects() {
//		this.primitiveObjects.clear()
//
//		for (child : children)
//			child.clearAllPrimitiveObjects()
//
//		for (clazz : clazzes)
//			clazz.clearAllPrimitiveObjects()
	}

	override void highlight() {
//		for (primitiveObject : this.primitiveObjects)
//			primitiveObject.highlight(ColorDefinitions::highlightColor)
//			
//		highlighted = true
	}

	override void unhighlight() {
//		if (highlighted) {
//			for (primitiveObject : this.primitiveObjects)
//				primitiveObject.unhighlight()
//				
//			highlighted = false
//		} else {
//		for (child : children)
//			child.unhighlight()
//
//		for (clazz : clazzes)
//			clazz.unhighlight()
//		}
	}
}
