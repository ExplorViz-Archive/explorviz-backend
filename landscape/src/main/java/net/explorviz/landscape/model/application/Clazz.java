package net.explorviz.landscape.model.application;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing a single class (instance during runtime within a single application).
 */
@SuppressWarnings("serial")
@Type("clazz")
public class Clazz extends BaseEntity {

  private String name;
  private String fullQualifiedName;
  private int instanceCount;
  private transient Set<Integer> objectIds = new HashSet<>();

  @Relationship("parent")
  private Component parent;

  @Relationship("outgoingClazzCommunications")
  private List<ClazzCommunication> outgoingClazzCommunications =
      new ArrayList<>();

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullQualifiedName() {
    return this.fullQualifiedName;
  }

  public void setFullQualifiedName(final String name) {
    this.fullQualifiedName = name;
  }

  public Set<Integer> getObjectIds() {
    return this.objectIds;
  }

  public void setObjectIds(final Set<Integer> objectIds) {
    this.objectIds = objectIds;
  }

  public Component getParent() {
    return this.parent;
  }

  public void setParent(final Component parent) {
    this.parent = parent;
  }

  public List<ClazzCommunication> getOutgoingClazzCommunications() {
    return this.outgoingClazzCommunications;
  }

  public void setOutgoingClazzCommunications(
      final List<ClazzCommunication> outgoingClazzCommunications) {
    this.outgoingClazzCommunications = outgoingClazzCommunications;
  }

  public void setInstanceCount(final int instanceCount) {
    this.instanceCount = instanceCount;
  }

  public int getInstanceCount() {
    return this.instanceCount;
  }

  /**
   * Clears all existings communication within the clazz.
   */
  public void reset() {
    this.instanceCount = 0;
    this.getObjectIds().clear();

    // Do we need this bi-directional reset due to JSON API converter?
    this.getOutgoingClazzCommunications().forEach((outgoingClazz) -> {
      outgoingClazz.reset();
    });

    this.getOutgoingClazzCommunications().clear();
  }

}
