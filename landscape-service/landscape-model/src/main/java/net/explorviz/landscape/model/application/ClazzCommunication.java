package net.explorviz.landscape.model.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.landscape.model.helper.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model representing communication between classes (within a single application).
 */
@SuppressWarnings("serial")
@Type("clazzcommunication")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "super.id")
public class ClazzCommunication extends BaseEntity {

  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(ClazzCommunication.class);

  @Relationship("sourceClazz")
  private Clazz sourceClazz;

  @Relationship("targetClazz")
  private Clazz targetClazz;

  @Relationship("traceSteps")
  private List<TraceStep> traceSteps = new ArrayList<>();

  private String operationName = "<unknown>";

  // added up requests (for all involved traces)
  private int totalRequests;

  // average response time (for all involved related tracesteps)
  private float averageResponseTime = 0;

  @JsonCreator
  public ClazzCommunication(@JsonProperty("id") final String id) {
    super(id);
  }

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

  public List<TraceStep> getTraceSteps() {
    return this.traceSteps;
  }

  public void setTraceSteps(final List<TraceStep> traceSteps) {
    this.traceSteps = traceSteps;
  }

  // returns a trace for a given traceId or creates a new one
  public Trace seekOrCreateTraceByTraceId(final String potentialNewTraceId,
      final Application application, final String traceId) {
    final List<Trace> traces = application.getTraces();

    for (final Trace trace : traces) {
      if (trace.getTraceId().equals(traceId)) {
        return trace;
      }
    }
    // create a new trace and refer it
    final Trace newTrace = new Trace(potentialNewTraceId, traceId);
    application.getTraces().add(newTrace);

    return newTrace;
  }

  // checks if a trace is existing and if not creates one and adds the runtime information
  public void addTraceStep(final String potentialNewTraceId, final String traceStepId,
      final Application application, final String traceId, final int tracePosition,
      final int requests, final float averageResponseTime, final float currentTraceDuration) {

    final Trace trace = this.seekOrCreateTraceByTraceId(potentialNewTraceId, application, traceId);
    final TraceStep newTraceStep = trace.addTraceStep(traceStepId, tracePosition, requests,
        averageResponseTime, currentTraceDuration, this);

    // reference the new trace for the application for easy access
    this.getTraceSteps().add(newTraceStep);
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
