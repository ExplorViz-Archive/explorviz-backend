package net.explorviz.model.helper;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.model.CommunicationClazz;
import net.explorviz.model.helper.Draw3DEdgeEntity;
import net.explorviz.model.helper.Draw3DNodeEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class CommunicationAppAccumulator extends Draw3DEdgeEntity {
  @Accessors
  private Draw3DNodeEntity source;
  
  @Accessors
  private Draw3DNodeEntity target;
  
  @Accessors
  private int requests;
  
  @Accessors
  private float averageResponseTime;
  
  @Accessors
  private final transient List<CommunicationClazz> aggregatedCommunications = new ArrayList<CommunicationClazz>();
  
  @Pure
  public Draw3DNodeEntity getSource() {
    return this.source;
  }
  
  public void setSource(final Draw3DNodeEntity source) {
    this.source = source;
  }
  
  @Pure
  public Draw3DNodeEntity getTarget() {
    return this.target;
  }
  
  public void setTarget(final Draw3DNodeEntity target) {
    this.target = target;
  }
  
  @Pure
  public int getRequests() {
    return this.requests;
  }
  
  public void setRequests(final int requests) {
    this.requests = requests;
  }
  
  @Pure
  public float getAverageResponseTime() {
    return this.averageResponseTime;
  }
  
  public void setAverageResponseTime(final float averageResponseTime) {
    this.averageResponseTime = averageResponseTime;
  }
  
  @Pure
  public List<CommunicationClazz> getAggregatedCommunications() {
    return this.aggregatedCommunications;
  }
}
