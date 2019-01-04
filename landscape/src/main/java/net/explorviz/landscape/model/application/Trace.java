package net.explorviz.landscape.model.application;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing a trace containing severals {@link TraceStep} between two {@link Clazz}.
 */
@SuppressWarnings("serial")
@Type("trace")
public class Trace extends BaseEntity {

  private long traceId;

  private int totalRequests;

  private float totalTraceDuration;

  private float averageResponseTime;

  @Relationship("traceSteps")
  private List<TraceStep> traceSteps = new ArrayList<>();


  public Trace(final long traceId) {
    this.setTraceId(traceId);
  }

  /**
   * Adds a new call within a trace as a {@link TraceStep}
   *
   * @param traceId
   * @param tracePosition
   * @param requests
   * @param averageResponseTime
   * @param currentTraceDuration
   * @param clazzCommunication
   * @return TraceStep
   */
  public TraceStep addTraceStep(final int tracePosition, final int requests,
      final float averageResponseTime, final float currentTraceDuration,
      final ClazzCommunication clazzCommunication) {

    final TraceStep newTraceStep = new TraceStep(this, clazzCommunication, tracePosition, requests,
        averageResponseTime, currentTraceDuration);

    final float beforeSum = this.getTotalRequests() * averageResponseTime;
    final float currentSum = requests * averageResponseTime;

    this.setAverageResponseTime((beforeSum + currentSum) / (this.getTotalRequests() + requests));
    this.setTotalTraceDuration(newTraceStep.getCurrentTraceDuration());
    this.setTotalRequests(this.getTotalRequests() + requests);

    this.getTraceSteps().add(newTraceStep);

    return newTraceStep;
  }

  public long getTraceId() {
    return this.traceId;
  }

  public void setTraceId(final long traceId) {
    this.traceId = traceId;
  }

  public List<TraceStep> getTraceSteps() {
    return this.traceSteps;
  }

  public void setTraceSteps(final List<TraceStep> traceSteps) {
    this.traceSteps = traceSteps;
  }

  public float getTotalTraceDuration() {
    return this.totalTraceDuration;
  }

  public void setTotalTraceDuration(final float totalTraceDuration) {
    this.totalTraceDuration = totalTraceDuration;
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

  public void setAverageResponseTime(final float averageResponseTime) {
    this.averageResponseTime = averageResponseTime;
  }

}