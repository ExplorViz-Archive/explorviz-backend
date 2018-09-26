package net.explorviz.model.application;

import com.github.jasminb.jsonapi.annotations.Type;
import java.util.HashSet;
import java.util.Set;
import net.explorviz.model.helper.BaseEntity;

/**
 * Model representing detailed runtime information for {@link ClazzCommunication} between two
 * {@link Clazz}.
 */
@SuppressWarnings("serial")
@Type("runtimeinformation")
public class RuntimeInformation extends BaseEntity {

  private long traceId;
  // in ns
  private float overallTraceDuration;
  private int requests;
  // in ns
  private float averageResponseTime;
  private final Set<Integer> orderIndexes = new HashSet<>();

  public long getTraceId() {
    return this.traceId;
  }

  public void setTraceId(final long traceId) {
    this.traceId = traceId;
  }

  public float getOverallTraceDuration() {
    return this.overallTraceDuration;
  }

  public void setOverallTraceDuration(final float overallTraceDuration) {
    this.overallTraceDuration = overallTraceDuration;
  }

  public int getRequests() {
    return this.requests;
  }

  public void setRequests(final int requests) {
    this.requests = requests;
  }

  public float getAverageResponseTimeInNanoSec() {
    return this.averageResponseTime;
  }

  public float getAverageResponseTime() {
    return this.averageResponseTime;
  }

  public void setAverageResponseTime(final float averageResponseTime) {
    this.averageResponseTime = averageResponseTime;
  }

  public Set<Integer> getOrderIndexes() {
    return this.orderIndexes;
  }

}
