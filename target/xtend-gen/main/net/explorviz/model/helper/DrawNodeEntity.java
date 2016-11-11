package net.explorviz.model.helper;

import net.explorviz.model.helper.BaseEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public abstract class DrawNodeEntity extends BaseEntity {
  @Accessors
  private String name;
  
  @Accessors
  private transient float width;
  
  @Accessors
  private transient float height;
  
  @Accessors
  private transient float positionX;
  
  @Accessors
  private transient float positionY;
  
  @Accessors
  private transient float positionZ;
  
  @Pure
  public String getName() {
    return this.name;
  }
  
  public void setName(final String name) {
    this.name = name;
  }
  
  @Pure
  public float getWidth() {
    return this.width;
  }
  
  public void setWidth(final float width) {
    this.width = width;
  }
  
  @Pure
  public float getHeight() {
    return this.height;
  }
  
  public void setHeight(final float height) {
    this.height = height;
  }
  
  @Pure
  public float getPositionX() {
    return this.positionX;
  }
  
  public void setPositionX(final float positionX) {
    this.positionX = positionX;
  }
  
  @Pure
  public float getPositionY() {
    return this.positionY;
  }
  
  public void setPositionY(final float positionY) {
    this.positionY = positionY;
  }
  
  @Pure
  public float getPositionZ() {
    return this.positionZ;
  }
  
  public void setPositionZ(final float positionZ) {
    this.positionZ = positionZ;
  }
}
