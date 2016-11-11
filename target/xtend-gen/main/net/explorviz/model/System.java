package net.explorviz.model;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.model.Landscape;
import net.explorviz.model.Node;
import net.explorviz.model.NodeGroup;
import net.explorviz.model.helper.DrawNodeEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class System extends DrawNodeEntity {
  @Accessors
  private List<NodeGroup> nodeGroups = new ArrayList<NodeGroup>();
  
  @Accessors
  private Landscape parent;
  
  private boolean opened = true;
  
  public boolean isOpened() {
    return this.opened;
  }
  
  public void setOpened(final boolean openedParam) {
    if (openedParam) {
      for (final NodeGroup nodeGroup : this.nodeGroups) {
        {
          nodeGroup.setVisible(true);
          List<Node> _nodes = nodeGroup.getNodes();
          int _size = _nodes.size();
          boolean _equals = (_size == 1);
          if (_equals) {
            nodeGroup.setOpened(true);
          } else {
            nodeGroup.setOpened(false);
          }
        }
      }
    } else {
      for (final NodeGroup nodeGroup_1 : this.nodeGroups) {
        {
          nodeGroup_1.setVisible(false);
          nodeGroup_1.setAllChildrenVisibility(false);
        }
      }
    }
    this.opened = openedParam;
  }
  
  @Pure
  public List<NodeGroup> getNodeGroups() {
    return this.nodeGroups;
  }
  
  public void setNodeGroups(final List<NodeGroup> nodeGroups) {
    this.nodeGroups = nodeGroups;
  }
  
  @Pure
  public Landscape getParent() {
    return this.parent;
  }
  
  public void setParent(final Landscape parent) {
    this.parent = parent;
  }
}
