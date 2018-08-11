package net.explorviz.model.landscape;

import java.util.ArrayList;
import java.util.List;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.BaseEntity;

/**
 * Model representing a system (a logical container for {@link NodeGroup} within
 * a software landscape)
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
@SuppressWarnings("serial")
@Type("system")
public class System extends BaseEntity {

	private String name;

	@Relationship("nodegroups")
	private final List<NodeGroup> nodeGroups = new ArrayList<NodeGroup>();

	@Relationship("parent")
	private Landscape parent;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Landscape getParent() {
		return parent;
	}

	public void setParent(final Landscape parent) {
		this.parent = parent;
	}

	public List<NodeGroup> getNodeGroups() {
		return nodeGroups;
	}

}