package net.explorviz.model;

import java.util.ArrayList;
import java.util.List;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.DrawNodeEntity;

@SuppressWarnings("serial")
@Type("node")
public class Node extends DrawNodeEntity {

	private String ipAddress;
	private double cpuUtilization;
	private long freeRAM;
	private long usedRAM;

	@Relationship("applications")
	private final List<Application> applications = new ArrayList<Application>();

	private boolean visible = true;

	@Relationship("parent")
	private NodeGroup parent;

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

	public void setVisible(final boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

}