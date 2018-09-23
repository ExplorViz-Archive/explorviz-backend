package net.explorviz.model.landscape;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.explorviz.model.helper.BaseEntity;
import net.explorviz.model.helper.NameComperator;

/**
 * Model representing a group of {@link Node} (a group of hosts within a software landscape).
 */
@SuppressWarnings("serial")
@Type("nodegroup")
public class NodeGroup extends BaseEntity {

  private String name;
  @Relationship("parent")
  private final List<Node> nodes = new ArrayList<Node>();

  @Relationship("parent")
  private System parent;

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public System getParent() {
    return parent;
  }

  public void setParent(final System parent) {
    this.parent = parent;
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public void updateName() {
    final List<String> allNames = getNodeNames();
    Collections.sort(allNames, new NameComperator());

    if (allNames.size() >= 2) {
      final String first = allNames.get(0);
      final String last = allNames.get(allNames.size() - 1);

      setName(first + " - " + last);
    } else if (allNames.size() == 1) {
      setName(allNames.get(0));
    } else {
      setName("<NO-NAME>");
    }
  }

  private List<String> getNodeNames() {
    final List<String> allNames = new ArrayList<String>();
    for (final Node node : nodes) {
      if (node.getName() != null && !node.getName().isEmpty() && !node.getName().startsWith("<")) {
        allNames.add(node.getName());
      } else {
        allNames.add(node.getIpAddress());
      }
    }
    return allNames;
  }
}
