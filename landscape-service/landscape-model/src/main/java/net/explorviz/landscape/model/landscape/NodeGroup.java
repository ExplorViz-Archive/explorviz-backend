package net.explorviz.landscape.model.landscape;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.explorviz.landscape.model.helper.BaseEntity;
import net.explorviz.landscape.model.helper.NameComperator;

/**
 * Model representing a group of {@link Node} (a group of hosts within a software landscape).
 */
@SuppressWarnings("serial")
@Type("nodegroup")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "super.id")
public class NodeGroup extends BaseEntity {

  private String name;

  @Relationship("nodes")
  private final List<Node> nodes = new ArrayList<>();

  @Relationship("parent")
  private System parent;

  @JsonCreator
  public NodeGroup(@JsonProperty("id") final String id) {
    super(id);
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public System getParent() {
    return this.parent;
  }

  public void setParent(final System parent) {
    this.parent = parent;
  }

  public List<Node> getNodes() {
    return this.nodes;
  }

  public void updateName() {
    final List<String> allNames = this.getNodeNames();
    Collections.sort(allNames, new NameComperator());

    if (allNames.size() >= 2) { // NOPMD
      final String first = allNames.get(0);
      final String last = allNames.get(allNames.size() - 1);

      this.setName(first + " - " + last);
    } else if (allNames.size() == 1) { // NOPMD
      this.setName(allNames.get(0));
    } else {
      this.setName("<NO-NAME>");
    }
  }

  private List<String> getNodeNames() {
    final List<String> allNames = new ArrayList<>();
    for (final Node node : this.nodes) {
      if (node.getName() != null && !node.getName().isEmpty() && !node.getName().startsWith("<")) { // NOPMD
        allNames.add(node.getName());
      } else {
        allNames.add(node.getIpAddress());
      }
    }
    return allNames;
  }
}
