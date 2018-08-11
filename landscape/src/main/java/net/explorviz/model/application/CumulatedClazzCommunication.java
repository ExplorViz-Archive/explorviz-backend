package net.explorviz.model.application;

import java.util.ArrayList;
import java.util.List;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.BaseEntity;

/**
 * Model representing cumulated communications between classes (both directions)
 * (within a single application)
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */

@SuppressWarnings("serial")
@Type("cumulatedclazzcommunication")
public class CumulatedClazzCommunication extends BaseEntity {

	private int requests = 0;

	@Relationship("sourceClazz")
	private Clazz sourceClazz;

	@Relationship("targetClazz")
	private Clazz targetClazz;

	@Relationship("aggregatedClazzCommunications")
	private List<AggregatedClazzCommunication> aggregatedClazzCommunications = new ArrayList<AggregatedClazzCommunication>();

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

	public List<AggregatedClazzCommunication> getAggregatedClazzCommunications() {
		return aggregatedClazzCommunications;
	}

	public void setAggregatedClazzCommunications(final List<AggregatedClazzCommunication> clazzCommunications) {
		this.aggregatedClazzCommunications = clazzCommunications;
	}

	// adds a clazzCommunication if sourceClazz and targetClazz matches or otherwise
	public boolean addAggregatedClazzCommunication(final AggregatedClazzCommunication aggClazzcommunication) {

		if (this.sourceClazz.equals(aggClazzcommunication.getSourceClazz())
				&& this.targetClazz.equals(aggClazzcommunication.getTargetClazz())
				|| this.sourceClazz.equals(aggClazzcommunication.getTargetClazz())
						&& this.targetClazz.equals(aggClazzcommunication.getSourceClazz())) {
			this.setRequests(this.getRequests() + aggClazzcommunication.getRequests());
			this.aggregatedClazzCommunications.add(aggClazzcommunication);
			return true;
		}
		return false;
	}

	public void reset() {
		requests = 0;
		aggregatedClazzCommunications.clear();
	}

}
