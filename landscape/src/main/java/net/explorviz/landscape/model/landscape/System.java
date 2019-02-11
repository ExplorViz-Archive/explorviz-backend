package net.explorviz.landscape.model.landscape;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing a system (a logical container for {@link NodeGroup} within a software
 * landscape).
 */
@SuppressWarnings("serial")
@Type("system")
public class System extends BaseEntity {

  private String name;

  @Relationship("nodegroups")
  private final List<NodeGroup> nodeGroups = new ArrayList<>();

  @Relationship("parent")
  private Landscape parent;


  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Landscape getParent() {
    return this.parent;
  }

  public void setParent(final Landscape parent) {
    this.parent = parent;
  }

  public List<NodeGroup> getNodeGroups() {
    return this.nodeGroups;
  }

}
