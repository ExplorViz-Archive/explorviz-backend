package net.explorviz.model;

import java.util.ArrayList
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.helper.DrawNodeEntity
import com.github.jasminb.jsonapi.annotations.Type
import com.github.jasminb.jsonapi.annotations.Relationship
import net.explorviz.math.Vector4f
import net.explorviz.model.helper.ColorDefinitions

@Type("node")
class Node extends DrawNodeEntity {
	@Accessors String ipAddress

	@Accessors double cpuUtilization
	@Accessors long freeRAM
	@Accessors long usedRAM

	@Relationship("applications")
	@Accessors List<Application> applications = new ArrayList<Application>

	@Accessors var boolean visible = true

	@Relationship("parent")
	@Accessors NodeGroup parent
	
	@Accessors Vector4f color = ColorDefinitions::nodeBackgroundColor
	
	new(String id) {
		this.id = id
	}

	public def String getDisplayName() {
		if (this.parent.opened) {
			if (this.name != null && !this.name.empty && !this.name.startsWith("<")) {
				this.name
			} else {
				this.ipAddress
			}
		} else {
			this.parent.name
		}
	}

}
