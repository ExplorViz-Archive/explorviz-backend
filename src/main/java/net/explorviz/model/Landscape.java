package net.explorviz.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.communication.ApplicationCommunication;
import net.explorviz.model.helper.BaseEntity;

@SuppressWarnings("serial")
@Type("landscape")
public class Landscape extends BaseEntity {

	private long activities;

	@Relationship("systems")
	private final List<System> systems = new ArrayList<System>();

	private final Map<Long, String> events = new TreeMap<Long, String>();
	private final Map<Long, String> errors = new TreeMap<Long, String>();

	public long getActivities() {
		return activities;
	}

	public void setActivities(final long activities) {
		this.activities = activities;
	}

	public List<System> getSystems() {
		return systems;
	}

	public Map<Long, String> getEvents() {
		return events;
	}

	public Map<Long, String> getErrors() {
		return errors;
	}

	public void updateTimestamp(final long timestamp) {
		setTimestamp(timestamp);
	}

	/**
	 * Returns all outgoing communication between applications within the landscape
	 */
	public List<ApplicationCommunication> getOutgoingApplicationCommunication() {

		final List<ApplicationCommunication> outgoingCommunicationList = new ArrayList<ApplicationCommunication>();

		for (final System system : this.getSystems()) {
			for (final NodeGroup nodegroup : system.getNodeGroups()) {
				for (final Node node : nodegroup.getNodes()) {
					for (final Application application : node.getApplications()) {
						for (final ApplicationCommunication outgoingCommunication : application
								.getOutgoingApplicationCommunications()) {
							outgoingCommunicationList.add(outgoingCommunication);
						}
					}
				}
			}
		}

		return outgoingCommunicationList;
	}

	/**
	 * Clears all existing communication within the landscape
	 */
	public void clearCommunication() {
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

}