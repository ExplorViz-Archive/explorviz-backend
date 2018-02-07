package net.explorviz.model;

import java.util.ArrayList;
import java.util.List;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.DrawNodeEntity;

@SuppressWarnings("serial")
@Type("system")
public class System extends DrawNodeEntity {

	@Relationship("nodegroups")
	private final List<NodeGroup> nodeGroups = new ArrayList<NodeGroup>();

	@Relationship("parent")
	private Landscape parent;

	private boolean opened = true;

	public Landscape getParent() {
		return parent;
	}

	public void setParent(final Landscape parent) {
		this.parent = parent;
	}

	public List<NodeGroup> getNodeGroups() {
		return nodeGroups;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(final boolean openedParam) {
		if (openedParam) {
			for (final NodeGroup nodeGroup : nodeGroups) {
				nodeGroup.setVisible(true);
				if (nodeGroup.getNodes().size() == 1) {
					nodeGroup.setOpened(true);
				} else {
					nodeGroup.setOpened(false);
				}
			}
		} else {
			for (final NodeGroup nodeGroup : nodeGroups) {
				nodeGroup.setVisible(false);
				nodeGroup.setAllChildrenVisibility(false);
			}
		}

		this.opened = openedParam;
	}

}