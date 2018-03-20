package net.explorviz.model.application;

import java.util.ArrayList;
import java.util.List;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.BaseEntity;

/**
 * Model representing aggregated communication between classes (from sourceClazz
 * to targetClazz) within a single application
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
@SuppressWarnings("serial")
@Type("aggregatedclazzcommunication")
public class AggregatedClazzCommunication extends BaseEntity {

	private int requests = 0;

	@Relationship("sourceClazz")
	private Clazz sourceClazz;

	@Relationship("targetClazz")
	private Clazz targetClazz;

	@Relationship("outgoingClazzCommunications")
	private List<ClazzCommunication> outgoingClazzCommunications = new ArrayList<ClazzCommunication>();

	public int getRequests() {
		return requests;
	}

	public void setRequests(final int requests) {
		this.requests = requests;
	}

	public Clazz getSourceClazz() {
		return sourceClazz;
	}

	public void setSourceClazz(final Clazz sourceClazz) {
		this.sourceClazz = sourceClazz;
	}

	public Clazz getTargetClazz() {
		return targetClazz;
	}

	public void setTargetClazz(final Clazz targetClazz) {
		this.targetClazz = targetClazz;
	}

	public List<ClazzCommunication> getOutgoingClazzCommunications() {
		return outgoingClazzCommunications;
	}

	public void setOutgoingClazzCommunications(final List<ClazzCommunication> clazzCommunications) {
		this.outgoingClazzCommunications = clazzCommunications;
	}

	// adds a clazzCommunication if sourceClazz and targetClazz matches
	public boolean addClazzCommunication(final ClazzCommunication clazzcommunication) {

		if (this.sourceClazz.equals(clazzcommunication.getSourceClazz())
				&& this.targetClazz.equals(clazzcommunication.getTargetClazz())) {
			this.setRequests(this.getRequests() + clazzcommunication.getRequests());
			this.outgoingClazzCommunications.add(clazzcommunication);
			return true;
		}
		return false;
	}

	public void reset() {
		requests = 0;
		outgoingClazzCommunications.clear();
	}

}