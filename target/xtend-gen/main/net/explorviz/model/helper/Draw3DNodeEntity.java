package net.explorviz.model.helper;

import net.explorviz.math.Vector3f;
import net.explorviz.model.helper.BaseEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public abstract class Draw3DNodeEntity extends BaseEntity {
  @Accessors
  private String name;
  
  @Accessors
  private String fullQualifiedName;
  
  @Accessors
  private transient float width;
  
  @Accessors
  private transient float height;
  
  @Accessors
  private transient float depth;
  
  @Accessors
  private transient float positionX;
  
  @Accessors
  private transient float positionY;
  
  @Accessors
  private transient float positionZ;
  
  private boolean highlighted = false;
  
  public boolean isHighlighted() {
    return this.highlighted;
  }
  
  public void setHighlighted(final boolean highlightedParam) {
    this.highlighted = highlightedParam;
  }
  
  public Vector3f getCenterPoint() {
    return new Vector3f((this.positionX + (this.width / 2f)), (this.positionY + (this.height / 2f)), 
      (this.positionZ + (this.depth / 2f)));
  }
  
  public Vector3f getExtension() {
    return new Vector3f((this.width / 2f), (this.height / 2f), (this.depth / 2f));
  }
  
  public Vector3f getPosition() {
    return new Vector3f(this.positionX, this.positionY, this.positionZ);
  }
  
  public abstract void highlight();
  
  public abstract void unhighlight();
  
  @Pure
  public String getName() {
    return this.name;
  }
  
  public void setName(final String name) {
    this.name = name;
  }
  
  @Pure
  public String getFullQualifiedName() {
    return this.fullQualifiedName;
  }
  
  public void setFullQualifiedName(final String fullQualifiedName) {
    this.fullQualifiedName = fullQualifiedName;
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
  public float getDepth() {
    return this.depth;
  }
  
  public void setDepth(final float depth) {
    this.depth = depth;
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
