package net.explorviz.model.landscape;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.application.Application;
import net.explorviz.model.application.ApplicationCommunication;
import net.explorviz.model.helper.BaseEntity;

/**
 * Model representing a software landscape
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
@SuppressWarnings("serial")
@Type("landscape")
public class Landscape extends BaseEntity {

	private long timestamp;
	private long overallCalls;

	@Relationship("systems")
	private final List<System> systems = new ArrayList<System>();

	private final Map<Long, String> events = new TreeMap<Long, String>();
	private final Map<Long, String> exceptions = new TreeMap<Long, String>();

	@Relationship("outgoingApplicationCommunications")
	private List<ApplicationCommunication> outgoingApplicationCommunications = new ArrayList<ApplicationCommunication>();

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public long getOverallCalls() {
		return overallCalls;
	}

	public void setOverallCalls(final long activities) {
		this.overallCalls = activities;
	}

	public List<System> getSystems() {
		return systems;
	}

	public Map<Long, String> getEvents() {
		return events;
	}

	public Map<Long, String> getExceptions() {
		return exceptions;
	}

	public void updateTimestamp(final long timestamp) {
		setTimestamp(timestamp);
	}

	public List<ApplicationCommunication> getOutgoingApplicationCommunications() {
		return outgoingApplicationCommunications;
	}

	public void setOutgoingApplicationCommunications(
			final List<ApplicationCommunication> outgoingApplicationCommunication) {
		this.outgoingApplicationCommunications = outgoingApplicationCommunication;
	}

	/**
	 * Clears all existing communication within the landscape
	 */
	private void clearCommunication() {

		// keeps applicationCommunication, but sets it to zero requests
		for (final ApplicationCommunication commu : this.getOutgoingApplicationCommunications()) {
			commu.reset();
		}

		for (final System system : this.getSystems()) {
			for (final NodeGroup nodegroup : system.getNodeGroups()) {
				for (final Node node : nodegroup.getNodes()) {
					for (final Application application : node.getApplications()) {
						application.clearCommunication();
					}
				}
			}
		}
	}

	/**
	 * Resets the landscape
	 */
	public void reset() {
		this.getExceptions().clear();
		this.getEvents().clear();
		this.setOverallCalls(0L);
		this.clearCommunication();
	}

}