package net.explorviz.model;

import com.google.common.base.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.explorviz.model.Clazz;
import net.explorviz.model.RuntimeInformation;
import net.explorviz.model.helper.BaseEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class CommunicationClazz extends BaseEntity {
  private int requestsCacheCount = 0;
  
  @Accessors
  private String methodName;
  
  @Accessors
  private Map<Long, RuntimeInformation> traceIdToRuntimeMap = new HashMap<Long, RuntimeInformation>();
  
  @Accessors
  private Clazz source;
  
  @Accessors
  private Clazz target;
  
  @Accessors
  private boolean hidden = false;
  
  public void addRuntimeInformation(final Long traceId, final int calledTimes, final int orderIndex, final int requests, final float averageResponseTime, final float overallTraceDuration) {
    RuntimeInformation runtime = this.traceIdToRuntimeMap.get(traceId);
    boolean _equals = Objects.equal(runtime, null);
    if (_equals) {
      RuntimeInformation _runtimeInformation = new RuntimeInformation();
      runtime = _runtimeInformation;
      runtime.setCalledTimes(calledTimes);
      Set<Integer> _orderIndexes = runtime.getOrderIndexes();
      _orderIndexes.add(Integer.valueOf(orderIndex));
      runtime.setRequests(requests);
      runtime.setOverallTraceDurationInNanoSec(overallTraceDuration);
      runtime.setAverageResponseTimeInNanoSec(averageResponseTime);
      this.traceIdToRuntimeMap.put(traceId, runtime);
      int _requestsCacheCount = this.requestsCacheCount;
      this.requestsCacheCount = (_requestsCacheCount + requests);
      return;
    }
    int _requests = runtime.getRequests();
    float _averageResponseTimeInNanoSec = runtime.getAverageResponseTimeInNanoSec();
    final float beforeSum = (_requests * _averageResponseTimeInNanoSec);
    final float currentSum = (requests * averageResponseTime);
    int _requests_1 = runtime.getRequests();
    int _plus = (_requests_1 + requests);
    float _divide = ((beforeSum + currentSum) / _plus);
    runtime.setAverageResponseTimeInNanoSec(_divide);
    int _requests_2 = runtime.getRequests();
    int _plus_1 = (_requests_2 + requests);
    runtime.setRequests(_plus_1);
    float _overallTraceDurationInNanoSec = runtime.getOverallTraceDurationInNanoSec();
    float _plus_2 = (overallTraceDuration + _overallTraceDurationInNanoSec);
    float _divide_1 = (_plus_2 / 2f);
    runtime.setOverallTraceDurationInNanoSec(_divide_1);
    Set<Integer> _orderIndexes_1 = runtime.getOrderIndexes();
    _orderIndexes_1.add(Integer.valueOf(orderIndex));
    int _requestsCacheCount_1 = this.requestsCacheCount;
    this.requestsCacheCount = (_requestsCacheCount_1 + requests);
  }
  
  public void reset() {
    this.requestsCacheCount = 0;
    this.traceIdToRuntimeMap.clear();
  }
  
  public int getRequests() {
    return this.requestsCacheCount;
  }
  
  @Pure
  public String getMethodName() {
    return this.methodName;
  }
  
  public void setMethodName(final String methodName) {
    this.methodName = methodName;
  }
  
  @Pure
  public Map<Long, RuntimeInformation> getTraceIdToRuntimeMap() {
    return this.traceIdToRuntimeMap;
  }
  
  public void setTraceIdToRuntimeMap(final Map<Long, RuntimeInformation> traceIdToRuntimeMap) {
    this.traceIdToRuntimeMap = traceIdToRuntimeMap;
  }
  
  @Pure
  public Clazz getSource() {
    return this.source;
  }
  
  public void setSource(final Clazz source) {
    this.source = source;
  }
  
  @Pure
  public Clazz getTarget() {
    return this.target;
  }
  
  public void setTarget(final Clazz target) {
    this.target = target;
  }
  
  @Pure
  public boolean isHidden() {
    return this.hidden;
  }
  
  public void setHidden(final boolean hidden) {
    this.hidden = hidden;
  }
}
