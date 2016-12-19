package net.explorviz.model;

import java.util.ArrayList
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import java.util.Collections
import java.util.Comparator
import net.explorviz.model.helper.DrawNodeEntity
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

@Type("nodegroup")
class NodeGroup extends DrawNodeEntity {
	
	@Relationship("parent")
	@Accessors List<Node> nodes = new ArrayList<Node>

	@Relationship("parent")
	@Accessors System parent

	@Accessors var boolean visible = true

//	public static val Vector4f plusColor = ColorDefinitions::nodeGroupPlusColor
//	public static val Vector4f backgroundColor = ColorDefinitions::nodeGroupBackgroundColor
	var boolean opened
	
	new(String id) {
		this.id = id
	}

	def boolean isOpened() {
		opened
	}

	def void setOpened(boolean openedParam) {
		if (openedParam) {
			setAllChildrenVisibility(true)
		} else {
			setAllChildrenVisibility(false)
			if (nodes.size() > 0) {
				val firstNode = nodes.get(0)
				firstNode.visible = true
			}
		}

		this.opened = openedParam
	}

	def void updateName() {
		val names = getAllNames
		Collections.sort(names, new NameComperator)

		if (names.size() >= 2) {
			val first = names.get(0)
			val last = names.get(names.size() - 1)

			name = first + " - " + last
		} else if (names.size() == 1) {
			name = names.get(0)
		} else {
			name = "<NO-NAME>"
		}
	}

	private def List<String> getAllNames() {
		val result = new ArrayList<String>()
		for (node : nodes) {
			if (node.name != null && !node.name.empty && !node.name.startsWith("<")) {
				result.add(node.name)
			} else {
				result.add(node.ipAddress)
			}
		}
		result
	}

	def setAllChildrenVisibility(boolean visiblity) {
		for (node : nodes)
			node.visible = visiblity
	}

	static class NameComperator implements Comparator<String> {
		override compare(String o1, String o2) {
			if (endsInNumber(o1) && endsInNumber(o2)) {
				val o1Number = getLastNumber(o1)
				val o2Number = getLastNumber(o2)

				return (o1Number - o2Number) as int
			} else {
				return o1.compareToIgnoreCase(o2)
			}
		}

		def getLastNumber(String arg) {
			var i = arg.length - 1
			var result = 0d
			var index = 0
			while (i >= 0 && isNumber(arg.charAt(i))) {
				val currentNumber = Integer.parseInt(arg.substring(i, i + 1))
				result = currentNumber * Math.pow(10, index) + result
				i = i - 1
				index = index + 1
			}

			result
		}

		def endsInNumber(String arg) {
			if (arg != null) {
				isNumber(arg.charAt(arg.length - 1))
			} else {
				false
			}
		}

		def isNumber(char c) {
			Character.isDigit(c)
		}

	}
}
