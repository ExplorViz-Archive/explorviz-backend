package net.explorviz.model;

import java.util.HashSet;
import java.util.Set;
import net.explorviz.model.helper.BaseEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class RuntimeInformation extends BaseEntity {
  @Accessors
  private int calledTimes;
  
  @Accessors
  private float overallTraceDurationInNanoSec;
  
  @Accessors
  private int requests;
  
  @Accessors
  private float averageResponseTimeInNanoSec;
  
  @Accessors
  private Set<Integer> orderIndexes = new HashSet<Integer>();
  
  @Pure
  public int getCalledTimes() {
    return this.calledTimes;
  }
  
  public void setCalledTimes(final int calledTimes) {
    this.calledTimes = calledTimes;
  }
  
  @Pure
  public float getOverallTraceDurationInNanoSec() {
    return this.overallTraceDurationInNanoSec;
  }
  
  public void setOverallTraceDurationInNanoSec(final float overallTraceDurationInNanoSec) {
    this.overallTraceDurationInNanoSec = overallTraceDurationInNanoSec;
  }
  
  @Pure
  public int getRequests() {
    return this.requests;
  }
  
  public void setRequests(final int requests) {
    this.requests = requests;
  }
  
  @Pure
  public float getAverageResponseTimeInNanoSec() {
    return this.averageResponseTimeInNanoSec;
  }
  
  public void setAverageResponseTimeInNanoSec(final float averageResponseTimeInNanoSec) {
    this.averageResponseTimeInNanoSec = averageResponseTimeInNanoSec;
  }
  
  @Pure
  public Set<Integer> getOrderIndexes() {
    return this.orderIndexes;
  }
  
  public void setOrderIndexes(final Set<Integer> orderIndexes) {
    this.orderIndexes = orderIndexes;
  }
}
