package net.explorviz.model;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.model.Application;
import net.explorviz.model.NodeGroup;
import net.explorviz.model.helper.DrawNodeEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class Node extends DrawNodeEntity {
  @Accessors
  private String ipAddress;
  
  @Accessors
  private double cpuUtilization;
  
  @Accessors
  private long freeRAM;
  
  @Accessors
  private long usedRAM;
  
  @Accessors
  private List<Application> applications = new ArrayList<Application>();
  
  @Accessors
  private boolean visible = true;
  
  @Accessors
  private NodeGroup parent;
  
  public String getDisplayName() {
    String _xifexpression = null;
    boolean _isOpened = this.parent.isOpened();
    if (_isOpened) {
      String _xifexpression_1 = null;
      boolean _and = false;
      boolean _and_1 = false;
      String _name = this.getName();
      boolean _notEquals = (!Objects.equal(_name, null));
      if (!_notEquals) {
        _and_1 = false;
      } else {
        String _name_1 = this.getName();
        boolean _isEmpty = _name_1.isEmpty();
        boolean _not = (!_isEmpty);
        _and_1 = _not;
      }
      if (!_and_1) {
        _and = false;
      } else {
        String _name_2 = this.getName();
        boolean _startsWith = _name_2.startsWith("<");
        boolean _not_1 = (!_startsWith);
        _and = _not_1;
      }
      if (_and) {
        _xifexpression_1 = this.getName();
      } else {
        _xifexpression_1 = this.ipAddress;
      }
      _xifexpression = _xifexpression_1;
    } else {
      _xifexpression = this.parent.getName();
    }
    return _xifexpression;
  }
  
  @Pure
  public String getIpAddress() {
    return this.ipAddress;
  }
  
  public void setIpAddress(final String ipAddress) {
    this.ipAddress = ipAddress;
  }
  
  @Pure
  public double getCpuUtilization() {
    return this.cpuUtilization;
  }
  
  public void setCpuUtilization(final double cpuUtilization) {
    this.cpuUtilization = cpuUtilization;
  }
  
  @Pure
  public long getFreeRAM() {
    return this.freeRAM;
  }
  
  public void setFreeRAM(final long freeRAM) {
    this.freeRAM = freeRAM;
  }
  
  @Pure
  public long getUsedRAM() {
    return this.usedRAM;
  }
  
  public void setUsedRAM(final long usedRAM) {
    this.usedRAM = usedRAM;
  }
  
  @Pure
  public List<Application> getApplications() {
    return this.applications;
  }
  
  public void setApplications(final List<Application> applications) {
    this.applications = applications;
  }
  
  @Pure
  public boolean isVisible() {
    return this.visible;
  }
  
  public void setVisible(final boolean visible) {
    this.visible = visible;
  }
  
  @Pure
  public NodeGroup getParent() {
    return this.parent;
  }
  
  public void setParent(final NodeGroup parent) {
    this.parent = parent;
  }
}
