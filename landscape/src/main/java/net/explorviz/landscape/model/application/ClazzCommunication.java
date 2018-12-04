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

  @Relationship("traces")
  private List<Trace> traces = new ArrayList<>();

  private String operationName = "<unknown>";

  // added up requests (for all involved traces)
  private int totalRequests;

  // average response time (for all involved related tracesteps)
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

  public void setTraces(final List<Trace> traces) {
    this.traces = traces;;
  }

  // returns a trace for a given traceId or creates a new one
  public Trace retrieveTraceByTraceId(final Long traceId) {
    for (final Trace trace : this.getTraces()) {
      if (trace.getId().equals(traceId)) {
        return trace;
      }
    }
    // create a new trace
    return new Trace(traceId);
  }

  // checks if a trace is existing and if not creates one and adds the runtime information
  public void addTraceStep(final Application application, final Long traceId,
      final int tracePosition, final int requests, final float averageResponseTime,
      final float currentTraceDuration) {

    final Trace trace = this.retrieveTraceByTraceId(traceId);
    trace.addTraceStep(tracePosition, requests, averageResponseTime, currentTraceDuration, this);

    // reference the new trace for the application for easy access
    this.getTraces().add(trace);
    application.getTraces().add(trace);
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
