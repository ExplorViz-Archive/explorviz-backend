package net.explorviz.landscape.model.landscape;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing a node (host within a software landscape).
 */
@SuppressWarnings("serial")
@Type("node")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "super.id")
public class Node extends BaseEntity {

  private String name;
  private String ipAddress;
  private double cpuUtilization;
  private long freeRAM;
  private long usedRAM;

  @Relationship("applications")
  private final List<Application> applications = new ArrayList<>();

  @Relationship("parent")
  private NodeGroup parent;

  @JsonCreator
  public Node(@JsonProperty("id") final String id) {
    super(id);
  }

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

  public void setFreeRAM(final long freeRAM) {
    this.freeRAM = freeRAM;
  }

  public long getFreeRAM() {
    return this.freeRAM;
  }

  public void setUsedRAM(final long usedRAM) {
    this.usedRAM = usedRAM;
  }

  public long getUsedRAM() {
    return this.usedRAM;
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
