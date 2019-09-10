package net.explorviz.landscape.model.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing detailed runtime information for {@link ClazzCommunication} between two
 * {@link Clazz} in a specific {@link Trace}.
 */
@SuppressWarnings("serial")
@Type("tracestep")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "super.id")
public class TraceStep extends BaseEntity {

  // related trace
  @Relationship("parentTrace")
  private Trace parentTrace;

  // position in the related trace (first position = 1)
  private int tracePosition = 0;

  @Relationship("clazzCommunication")
  private ClazzCommunication clazzCommunication;

  private int requests;

  private float currentTraceDuration;

  private float averageResponseTime;

  /**
   * Creates a new TraceStep and assigns related communication information.
   *
   * @param parentTrace - related Trace
   * @param clazzCommunication - starting clazzCommunication
   * @param tracePosition - position within the trace
   * @param requests - total number of requests
   * @param averageResponseTime - average response time of the trace
   * @param currentTraceDuration - current duration of the trace
   *
   *
   */
  @JsonCreator
  public TraceStep(@JsonProperty("id") final String id,
      @JsonProperty("parentTrace") final Trace parentTrace,
      @JsonProperty("clazzCommunication") final ClazzCommunication clazzCommunication,
      @JsonProperty("tracePosition") final int tracePosition,
      @JsonProperty("requests") final int requests,
      @JsonProperty("averageResponseTime") final float averageResponseTime,
      @JsonProperty("currentTraceDuration") final float currentTraceDuration) {
    super(id);
    this.setParentTrace(parentTrace);
    this.setClazzCommunication(clazzCommunication);
    this.setTracePosition(tracePosition);
    this.setAverageResponseTime(averageResponseTime);
    this.setRequests(requests);
    this.setCurrentTraceDuration(currentTraceDuration);
  }

  public Trace getParentTrace() {
    return this.parentTrace;
  }

  public void setParentTrace(final Trace parentTrace) {
    this.parentTrace = parentTrace;
  }

  public Integer getTracePosition() {
    return this.tracePosition;
  }

  public void setTracePosition(final int tracePosition) {
    this.tracePosition = tracePosition;
  }

  public float getCurrentTraceDuration() {
    return this.currentTraceDuration;
  }

  public ClazzCommunication getClazzCommunication() {
    return this.clazzCommunication;
  }

  public void setClazzCommunication(final ClazzCommunication clazzCommunication) {
    this.clazzCommunication = clazzCommunication;
  }

  public int getRequests() {
    return this.requests;
  }

  public void setRequests(final int requests) {
    this.requests = requests;
  }

  public void setCurrentTraceDuration(final float currentTraceDuration) {
    this.currentTraceDuration = currentTraceDuration;
  }

  public float getAverageResponseTime() {
    return this.averageResponseTime;
  }

  public void setAverageResponseTime(final float averageResponseTime) {
    this.averageResponseTime = averageResponseTime;
  }

}
