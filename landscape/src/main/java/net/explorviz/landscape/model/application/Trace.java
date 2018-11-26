package net.explorviz.landscape.model.application;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing a trace containing severals {@link RuntimeInformation} between two
 * {@link Clazz}.
 */
@SuppressWarnings("serial")
@Type("trace")
public class Trace extends BaseEntity {

  private long traceId;

  private float totalTraceDuration;

  private int totalRequests;

  @Relationship("runtimeInformations")
  private final List<RuntimeInformation> runtimeInformations = new ArrayList<>();


  public Trace(final long traceId) {
    this.setTraceId(traceId);
  }

  // checks if a trace with a corresponding traceId exists
  @Override
  public boolean equals(final Object object) {
    boolean result = false;

    if (object == null || object.getClass() != this.getClass()) {
      result = false;
    } else {
      final Trace trace = (Trace) object;
      if (((Long) this.getTraceId()).equals(trace.getTraceId())) {
        result = true;
      }
    }
    return result;
  }

  // returns a runtimeInformation for a given clazzCommunication or creates a new one
  public RuntimeInformation retrieveRuntimeInformationByClazzCommunication(
      final ClazzCommunication clazzCommunication) {
    for (final RuntimeInformation runtimeInformation : this.getRuntimeInformations()) {
      if (runtimeInformation.equals(clazzCommunication)) {
        return runtimeInformation;
      }
    }
    // create a new runtimeInformation
    return new RuntimeInformation(clazzCommunication);
  }

  public void addRuntimeInformation(final Long traceId, final int tracePosition, final int requests,
      final float averageResponseTime, final float currentTraceDuration,
      final ClazzCommunication clazzCommunication) {

    final List<RuntimeInformation> runtimeInformations = this.getRuntimeInformations();
    RuntimeInformation lastRuntimeInformation = null; // NO PMD

    final RuntimeInformation runtimeInformation =
        this.retrieveRuntimeInformationByClazzCommunication(clazzCommunication);

    // retrieve the last runtime for aggregation purposes
    if (!runtimeInformations.isEmpty()) {
      lastRuntimeInformation = runtimeInformations.get(runtimeInformations.size() - 1);

      final float beforeSum =
          lastRuntimeInformation.getRequests() * lastRuntimeInformation.getAverageResponseTime();
      final float currentSum = requests * averageResponseTime;

      runtimeInformation.setAverageResponseTime(
          (beforeSum + currentSum) / (lastRuntimeInformation.getRequests() + requests));
      runtimeInformation.setRequests(lastRuntimeInformation.getRequests() + requests);
      runtimeInformation.setCurrentTraceDuration(
          (currentTraceDuration + lastRuntimeInformation.getCurrentTraceDuration()) / 2f);
      runtimeInformation.setTracePosition(tracePosition);

      this.setTotalTraceDuration(runtimeInformation.getCurrentTraceDuration());
      this.setTotalRequests(lastRuntimeInformation.getRequests() + requests);

      return;
    }

    // if no related runtimeInformation exists
    runtimeInformation.setTrace(this);
    runtimeInformation.setTracePosition(tracePosition);
    runtimeInformation.setRequests(requests);
    runtimeInformation.setCurrentTraceDuration(currentTraceDuration);
    runtimeInformation.setAverageResponseTime(averageResponseTime);

    this.runtimeInformations.add(runtimeInformation);
  }

  public long getTraceId() {
    return this.traceId;
  }

  public void setTraceId(final long traceId) {
    this.traceId = traceId;
  }

  public List<RuntimeInformation> getRuntimeInformations() {
    return this.runtimeInformations;
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

}
