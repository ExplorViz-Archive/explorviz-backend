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