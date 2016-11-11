package net.explorviz.model;

import net.explorviz.model.Application;
import net.explorviz.model.Clazz;
import net.explorviz.model.helper.DrawEdgeEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class Communication extends DrawEdgeEntity {
  @Accessors
  private int requests;
  
  @Accessors
  private String technology;
  
  @Accessors
  private float averageResponseTimeInNanoSec;
  
  @Accessors
  private Application source;
  
  @Accessors
  private Application target;
  
  @Accessors
  private Clazz sourceClazz;
  
  @Accessors
  private Clazz targetClazz;
  
  @Pure
  public int getRequests() {
    return this.requests;
  }
  
  public void setRequests(final int requests) {
    this.requests = requests;
  }
  
  @Pure
  public String getTechnology() {
    return this.technology;
  }
  
  public void setTechnology(final String technology) {
    this.technology = technology;
  }
  
  @Pure
  public float getAverageResponseTimeInNanoSec() {
    return this.averageResponseTimeInNanoSec;
  }
  
  public void setAverageResponseTimeInNanoSec(final float averageResponseTimeInNanoSec) {
    this.averageResponseTimeInNanoSec = averageResponseTimeInNanoSec;
  }
  
  @Pure
  public Application getSource() {
    return this.source;
  }
  
  public void setSource(final Application source) {
    this.source = source;
  }
  
  @Pure
  public Application getTarget() {
    return this.target;
  }
  
  public void setTarget(final Application target) {
    this.target = target;
  }
  
  @Pure
  public Clazz getSourceClazz() {
    return this.sourceClazz;
  }
  
  public void setSourceClazz(final Clazz sourceClazz) {
    this.sourceClazz = sourceClazz;
  }
  
  @Pure
  public Clazz getTargetClazz() {
    return this.targetClazz;
  }
  
  public void setTargetClazz(final Clazz targetClazz) {
    this.targetClazz = targetClazz;
  }
}
