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

  private int requests;
  private String operationName = "<unknown>";

  @Relationship("runtimeInformations")
  private final List<RuntimeInformation> runtimeInformations = new ArrayList<>();

  @Relationship("sourceClazz")
  private Clazz sourceClazz;

  @Relationship("targetClazz")
  private Clazz targetClazz;

  public int getRequests() {
    return this.requests;
  }

  public void setRequests(final int requests) {
    this.requests = requests;
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

  public List<RuntimeInformation> getRuntimeInformations() {
    return this.runtimeInformations;
  }

  public void addRuntimeInformation(final Long traceId, final int orderIndex, final int requests,
      final float averageResponseTime, final float overallTraceDuration) {

    RuntimeInformation runtime = null; // NOPMD

    for (final RuntimeInformation runtimeInformation : this.runtimeInformations) {
      if (runtimeInformation.getTraceId() == traceId) {
        runtime = runtimeInformation; // NOPMD
      }
    }

    if (runtime == null) {
      runtime = new RuntimeInformation();
      runtime.initializeId();
      runtime.setTraceId(traceId);
      runtime.getOrderIndexes().add(orderIndex);
      runtime.setRequests(requests);
      runtime.setOverallTraceDuration(overallTraceDuration);
      runtime.setAverageResponseTime(averageResponseTime);

      this.runtimeInformations.add(runtime);
      this.setRequests(this.getRequests() + requests);
      return;
    }

    final float beforeSum = runtime.getRequests() * runtime.getAverageResponseTimeInNanoSec();
    final float currentSum = requests * averageResponseTime;

    runtime.setAverageResponseTime((beforeSum + currentSum) / (runtime.getRequests() + requests));
    runtime.setRequests(runtime.getRequests() + requests);
    runtime
        .setOverallTraceDuration((overallTraceDuration + runtime.getOverallTraceDuration()) / 2f);
    runtime.getOrderIndexes().add(orderIndex);
    this.setRequests(this.getRequests() + requests);
  }

  public void reset() {
    this.requests = 0;
    this.runtimeInformations.clear();
  }

}
