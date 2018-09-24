package net.explorviz.model.application;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.model.helper.BaseEntity;

/**
 * Model representing communication between classes (within a single application).
 */
@SuppressWarnings("serial")
@Type("clazzcommunication")
public class ClazzCommunication extends BaseEntity {

  private int requests = 0;
  private String operationName = "<unknown>";

  @Relationship("runtimeInformations")
  private final List<RuntimeInformation> runtimeInformations = new ArrayList<RuntimeInformation>();

  @Relationship("sourceClazz")
  private Clazz sourceClazz;

  @Relationship("targetClazz")
  private Clazz targetClazz;

  public int getRequests() {
    return requests;
  }

  public void setRequests(final int requests) {
    this.requests = requests;
  }

  public String getOperationName() {
    return operationName;
  }

  public void setOperationName(final String methodName) {
    this.operationName = methodName;
  }

  public Clazz getSourceClazz() {
    return sourceClazz;
  }

  public void setSourceClazz(final Clazz sourceClazz) {
    this.sourceClazz = sourceClazz;
  }

  public Clazz getTargetClazz() {
    return targetClazz;
  }

  public void setTargetClazz(final Clazz targetClazz) {
    this.targetClazz = targetClazz;
  }

  public List<RuntimeInformation> getRuntimeInformations() {
    return runtimeInformations;
  }

  public void addRuntimeInformation(final Long traceId, final int orderIndex, final int requests,
      final float averageResponseTime, final float overallTraceDuration) {

    RuntimeInformation runtime = null;

    for (final RuntimeInformation runtimeInformation : runtimeInformations) {
      if (runtimeInformation.getTraceId() == traceId) {
        runtime = runtimeInformation;
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

      runtimeInformations.add(runtime);
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
    requests = 0;
    runtimeInformations.clear();
  }

}
