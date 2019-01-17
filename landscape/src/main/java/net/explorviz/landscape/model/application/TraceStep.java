package net.explorviz.landscape.model.application;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing detailed runtime information for {@link ClazzCommunication} between two
 * {@link Clazz} in a specific {@link Trace}.
 */
@SuppressWarnings("serial")
@Type("tracestep")
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
   * @param clazzCommunication the class communication
   * @param currentTraceDuration the current duration
   * @param averageResponseTime the average response time
   * @param requests the request
   * @param tracePosition the position of the trace
   */
  public TraceStep(final Trace parentTrace, final ClazzCommunication clazzCommunication,
      final int tracePosition, final int requests, final float averageResponseTime,
      final float currentTraceDuration) {
    this.setParentTrace(parentTrace);
    this.setClazzCommunication(clazzCommunication);
    this.setTracePosition(tracePosition);
    this.setAverageResponseTime(averageResponseTime);
    this.setRequests(requests);
    this.setCurrentTraceDuration(currentTraceDuration);
  }

  public TraceStep() {} // Jackson

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
