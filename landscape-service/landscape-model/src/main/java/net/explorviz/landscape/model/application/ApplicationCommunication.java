package net.explorviz.landscape.model.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing communication between two {@link Application}.
 */
@SuppressWarnings("serial")
@Type("applicationcommunication")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "super.id")
public class ApplicationCommunication extends BaseEntity {

  private int requests;
  private String technology;
  private float averageResponseTime;

  @Relationship("sourceApplication")
  private Application sourceApplication;

  @Relationship("targetApplication")
  private Application targetApplication;

  @Relationship("sourceClazz")
  private Clazz sourceClazz;

  @Relationship("targetClazz")
  private Clazz targetClazz;

  @JsonCreator
  public ApplicationCommunication(@JsonProperty("id") final String id) {
    super(id);
  }

  public int getRequests() {
    return this.requests;
  }

  public void setRequests(final int requests) {
    this.requests = requests;
  }

  public String getTechnology() {
    return this.technology;
  }

  public void setTechnology(final String technology) {
    this.technology = technology;
  }

  public float getAverageResponseTime() {
    return this.averageResponseTime;
  }

  public void setAverageResponseTime(final float averageResponseTime) {
    this.averageResponseTime = averageResponseTime;
  }

  public Application getSourceApplication() {
    return this.sourceApplication;
  }

  public void setSourceApplication(final Application sourceApplication) {
    this.sourceApplication = sourceApplication;
  }

  public Application getTargetApplication() {
    return this.targetApplication;
  }

  public void setTargetApplication(final Application targetApplication) {
    this.targetApplication = targetApplication;
  }

  public Clazz getSourceClazz() {
    return this.sourceClazz;
  }

  public void setSourceClazz(final Clazz sourceClazz) {
    this.sourceClazz = sourceClazz;
  }

  public Clazz getTargetClazz() {
    return this.targetClazz;
  }

  public void setTargetClazz(final Clazz targetClazz) {
    this.targetClazz = targetClazz;
  }

  public void reset() {
    this.requests = 0;
    this.averageResponseTime = 0;
  }

}
