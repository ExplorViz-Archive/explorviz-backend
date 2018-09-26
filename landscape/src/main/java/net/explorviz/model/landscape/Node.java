package net.explorviz.model.landscape;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.model.application.Application;
import net.explorviz.model.helper.BaseEntity;

/**
 * Model representing a node (host within a software landscape).
 */
@SuppressWarnings("serial")
@Type("node")
public class Node extends BaseEntity {

  private String name;
  private String ipAddress;
  private double cpuUtilization;
  private long freeRam;
  private long usedRam;

  @Relationship("applications")
  private final List<Application> applications = new ArrayList<>();

  @Relationship("parent")
  private NodeGroup parent;

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getIpAddress() {
    return this.ipAddress;
  }

  public void setIpAddress(final String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public NodeGroup getParent() {
    return this.parent;
  }

  public void setParent(final NodeGroup parent) {
    this.parent = parent;
  }

  public void setCpuUtilization(final double cpuUtilization) {
    this.cpuUtilization = cpuUtilization;
  }

  public double getCpuUtilization() {
    return this.cpuUtilization;
  }

  public void setFreeRam(final long freeRam) {
    this.freeRam = freeRam;
  }

  public long getFreeRam() {
    return this.freeRam;
  }

  public void setUsedRam(final long usedRam) {
    this.usedRam = usedRam;
  }

  public long getUsedRam() {
    return this.usedRam;
  }

  public List<Application> getApplications() {
    return this.applications;
  }

  @JsonIgnore
  public String getDisplayName() {
    final String displayName = this.name == null ? this.ipAddress : this.name;
    return displayName == null ? "This node has not been configured correctly" : displayName;
  }

}
