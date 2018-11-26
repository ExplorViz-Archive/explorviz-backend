package net.explorviz.landscape.model.application;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing communication between classes (within a single application).
 */
@SuppressWarnings("serial")
@Type("clazzcommunication")
public class ClazzCommunication extends BaseEntity {

  @Relationship("sourceClazz")
  private Clazz sourceClazz;

  @Relationship("targetClazz")
  private Clazz targetClazz;

  @Relationship("trace")
  private final List<Trace> traces = new ArrayList<>();

  private String operationName = "<unknown>";

  // added up requests (for all involved traces)
  private int totalRequests;

  // average response time (for all involved traces)
  private float averageResponseTime = 0;


  public String getOperationName() {
    return this.operationName;
  }

  public void setOperationName(final String methodName) {
    this.operationName = methodName;
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

  public List<Trace> getTraces() {
    return this.traces;
  }

  // returns a trace for a given traceId or creates a new one
  public Trace retrieveTraceByTraceId(final Long traceId) {
    for (final Trace trace : this.traces) {
      if (trace.equals(traceId)) {
        return trace;
      }
    }
    // create a new trace
    return new Trace(traceId);
  }

  // checks if a trace is existing and if not creates one and adds the runtime information
  public void addTrace(final Long traceId, final int tracePosition, final int requests,
      final float averageResponseTime, final float currentTraceDuration) {

    final Trace trace = this.retrieveTraceByTraceId(traceId);
    trace.addRuntimeInformation(traceId, tracePosition, requests, averageResponseTime,
        currentTraceDuration, this);
  }

  public int getTotalRequests() {
    return this.totalRequests;
  }

  public void setTotalRequests(final int totalRequests) {
    this.totalRequests = totalRequests;
  }

  public float getAverageResponseTime() {
    return this.averageResponseTime;
  }

  public void setAverageResponseTime(final float averageRepsonseTime) {
    this.averageResponseTime = averageRepsonseTime;
  }

  public void reset() {
    this.totalRequests = 0;
    this.averageResponseTime = 0;

  }

}
