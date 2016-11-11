package net.explorviz.model;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.math.Vector4f;
import net.explorviz.model.Application;
import net.explorviz.model.Clazz;
import net.explorviz.model.helper.Draw3DNodeEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class Component extends Draw3DNodeEntity {
  @Accessors
  private String name;
  
  @Accessors
  private String fullQualifiedName;
  
  @Accessors
  private boolean synthetic = false;
  
  @Accessors
  private boolean foundation = false;
  
  @Accessors
  private List<Component> children = new ArrayList<Component>();
  
  @Accessors
  private List<Clazz> clazzes = new ArrayList<Clazz>();
  
  @Accessors
  private Component parentComponent;
  
  @Accessors
  private Application belongingApplication;
  
  @Accessors
  private Vector4f color;
  
  private boolean opened = false;
  
  public boolean isOpened() {
    return this.opened;
  }
  
  public void setOpened(final boolean openedParam) {
    if ((!openedParam)) {
      this.setAllChildrenUnopened();
    }
    this.opened = openedParam;
  }
  
  private void setAllChildrenUnopened() {
    for (final Component child : this.children) {
      child.setOpened(false);
    }
  }
  
  public void openAllComponents() {
    this.opened = true;
    for (final Component child : this.children) {
      child.openAllComponents();
    }
  }
  
  public void closeAllComponents() {
    this.opened = false;
    for (final Component child : this.children) {
      child.closeAllComponents();
    }
  }
  
  public void clearAllPrimitiveObjects() {
  }
  
  @Override
  public void highlight() {
  }
  
  @Override
  public void unhighlight() {
  }
  
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
  public boolean isSynthetic() {
    return this.synthetic;
  }
  
  public void setSynthetic(final boolean synthetic) {
    this.synthetic = synthetic;
  }
  
  @Pure
  public boolean isFoundation() {
    return this.foundation;
  }
  
  public void setFoundation(final boolean foundation) {
    this.foundation = foundation;
  }
  
  @Pure
  public List<Component> getChildren() {
    return this.children;
  }
  
  public void setChildren(final List<Component> children) {
    this.children = children;
  }
  
  @Pure
  public List<Clazz> getClazzes() {
    return this.clazzes;
  }
  
  public void setClazzes(final List<Clazz> clazzes) {
    this.clazzes = clazzes;
  }
  
  @Pure
  public Component getParentComponent() {
    return this.parentComponent;
  }
  
  public void setParentComponent(final Component parentComponent) {
    this.parentComponent = parentComponent;
  }
  
  @Pure
  public Application getBelongingApplication() {
    return this.belongingApplication;
  }
  
  public void setBelongingApplication(final Application belongingApplication) {
    this.belongingApplication = belongingApplication;
  }
  
  @Pure
  public Vector4f getColor() {
    return this.color;
  }
  
  public void setColor(final Vector4f color) {
    this.color = color;
  }
}
