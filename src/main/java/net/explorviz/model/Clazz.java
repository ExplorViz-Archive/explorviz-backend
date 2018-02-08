package net.explorviz.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.communication.ClazzCommunication;
import net.explorviz.model.helper.Draw3DNodeEntity;

@SuppressWarnings("serial")
@Type("clazz")
public class Clazz extends Draw3DNodeEntity {

	private int instanceCount;
	private transient Set<Integer> objectIds = new HashSet<Integer>();

	@Relationship("parent")
	private Component parent;

	@Relationship("outgoingCommunications")
	private List<ClazzCommunication> outgoingClazzCommunications = new ArrayList<ClazzCommunication>();

	private final boolean visible = false;

	public Set<Integer> getObjectIds() {
		return objectIds;
	}

	public void setObjectIds(final Set<Integer> objectIds) {
		this.objectIds = objectIds;
	}

	public Component getParent() {
		return parent;
	}

	public void setParent(final Component parent) {
		this.parent = parent;
	}

	public void setOutgoingCommunications(final List<ClazzCommunication> outgoingCommunications) {
		this.outgoingClazzCommunications = outgoingCommunications;
	}

	public void setInstanceCount(final int instanceCount) {
		this.instanceCount = instanceCount;
	}

	public int getInstanceCount() {
		return instanceCount;
	}

	public List<ClazzCommunication> getOutgoingCommunications() {
		return outgoingClazzCommunications;
	}

	public boolean isVisible() {
		return visible;
	}

	/**
	 * Clears all existings communication within the clazz
	 */
	public void clearCommunication() {
		for (final ClazzCommunication clazzCommu : this.getOutgoingCommunications()) {
			clazzCommu.setSourceClazz(null);
			clazzCommu.setTargetClazz(null);
		}
		this.setOutgoingCommunications(new ArrayList<ClazzCommunication>());
	}

}