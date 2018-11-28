package net.explorviz.landscape.model.application;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing detailed runtime information for {@link ClazzCommunication} between two
 * {@link Clazz} in a specific {@link Trace}.
 */
@SuppressWarnings("serial")
@Type("tracedetail")
public class TraceStep extends BaseEntity {

  // related trace
  @Relationship("parentTrace")
  private Trace parentTrace;

  // position in the related trace (first position = 1)
  private int tracePosition = 1;

  @Relationship("clazzCommunication")
  private ClazzCommunication clazzCommunication;

  private int requests;

  private float currentTraceDuration;

  private float averageResponseTime;


  public TraceStep(final ClazzCommunication clazzCommunication) {
    this.setClazzCommunication(clazzCommunication);
  }

  // checks if a a clazz communication has the same source and target clazzes
  @Override
  public boolean equals(final Object object) {
    boolean result = false;

    if (object == null || object.getClass() != this.getClass()) {
      result = false;
    } else {
      final ClazzCommunication clazzCommunication = (ClazzCommunication) object;
      if (this.getClazzCommunication().equals(clazzCommunication)) {
        result = true;
      }
    }
    return result;
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
