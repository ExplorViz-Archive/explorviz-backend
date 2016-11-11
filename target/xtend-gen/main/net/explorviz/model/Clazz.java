package net.explorviz.model;

import java.util.HashSet;
import java.util.Set;
import net.explorviz.model.Component;
import net.explorviz.model.helper.Draw3DNodeEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class Clazz extends Draw3DNodeEntity {
  @Accessors
  private int instanceCount = 0;
  
  @Accessors
  private final transient Set<Integer> objectIds = new HashSet<Integer>();
  
  @Accessors
  private Component parent;
  
  @Accessors
  private boolean visible = false;
  
  public void clearAllPrimitiveObjects() {
  }
  
  @Override
  public void highlight() {
  }
  
  @Override
  public void unhighlight() {
  }
  
  @Pure
  public int getInstanceCount() {
    return this.instanceCount;
  }
  
  public void setInstanceCount(final int instanceCount) {
    this.instanceCount = instanceCount;
  }
  
  @Pure
  public Set<Integer> getObjectIds() {
    return this.objectIds;
  }
  
  @Pure
  public Component getParent() {
    return this.parent;
  }
  
  public void setParent(final Component parent) {
    this.parent = parent;
  }
  
  @Pure
  public boolean isVisible() {
    return this.visible;
  }
  
  public void setVisible(final boolean visible) {
    this.visible = visible;
  }
}
