package net.explorviz.model;

import java.util.ArrayList
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.helper.DrawNodeEntity

class System extends DrawNodeEntity {
	@Accessors List<NodeGroup> nodeGroups = new ArrayList<NodeGroup>

	@Accessors Landscape parent

	var boolean opened = true

//	public static val Vector4f plusColor = ColorDefinitions::systemPlusColor
//	public static val Vector4f foregroundColor = ColorDefinitions::systemForegroundColor
//	public static val Vector4f backgroundColor = ColorDefinitions::systemBackgroundColor
	def boolean isOpened() {
		opened
	}

	def void setOpened(boolean openedParam) {
		if (openedParam) {
			for (nodeGroup : nodeGroups) {
				nodeGroup.visible = true
				if (nodeGroup.getNodes().size() == 1) {
					nodeGroup.setOpened(true);
				} else {
					nodeGroup.setOpened(false);
				}
			}
		} else {
			for (nodeGroup : nodeGroups) {
				nodeGroup.visible = false
				nodeGroup.setAllChildrenVisibility(false)
			}
		}

		this.opened = openedParam
	}

}
