package net.explorviz.model.landscape;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.application.Application;
import net.explorviz.model.helper.BaseEntity;

/**
 * Model representing a node (host within a software landscape)
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
@SuppressWarnings("serial")
@Type("node")
public class Node extends BaseEntity {

	private String name;
	private String ipAddress;
	private double cpuUtilization = 0;
	private long freeRAM = 0;
	private long usedRAM = 0;

	@Relationship("applications")
	private final List<Application> applications = new ArrayList<Application>();

	@Relationship("parent")
	private NodeGroup parent;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public NodeGroup getParent() {
		return parent;
	}

	public void setParent(final NodeGroup parent) {
		this.parent = parent;
	}

	public void setCpuUtilization(final double cpuUtilization) {
		this.cpuUtilization = cpuUtilization;
	}

	public double getCpuUtilization() {
		return cpuUtilization;
	}

	public void setFreeRAM(final long freeRAM) {
		this.freeRAM = freeRAM;
	}

	public long getFreeRAM() {
		return freeRAM;
	}

	public void setUsedRAM(final long usedRAM) {
		this.usedRAM = usedRAM;
	}

	public long getUsedRAM() {
		return usedRAM;
	}

	public List<Application> getApplications() {
		return applications;
	}

	@JsonIgnore
	public String getDisplayName() {
		final String displayName = this.name == null ? this.ipAddress : this.name;
		return displayName == null ? "This node has not been configured correctly" : displayName;
	}

}