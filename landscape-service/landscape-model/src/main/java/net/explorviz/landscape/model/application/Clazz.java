package net.explorviz.landscape.model.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing a single class (instance during runtime within a single application).
 */
@SuppressWarnings("serial")
@Type("clazz")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "super.id")
public class Clazz extends BaseEntity {

  private String name;
  private String fullQualifiedName;
  private int instanceCount;

  @Relationship("parent")
  private Component parent;

  @Relationship("clazzCommunications")
  private List<ClazzCommunication> clazzCommunications = new ArrayList<>();

  @JsonCreator
  public Clazz(@JsonProperty("id") final String id) {
    super(id);
  }

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

  public Component getParent() {
    return this.parent;
  }

  public void setParent(final Component parent) {
    this.parent = parent;
  }

  public List<ClazzCommunication> getClazzCommunications() {
    return this.clazzCommunications;
  }

  public void setClazzCommunications(final List<ClazzCommunication> clazzCommunications) {
    this.clazzCommunications = clazzCommunications;
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

    // TODO Do we need this bi-directional reset due to JSON API converter?
    this.getClazzCommunications().forEach((outgoingClazz) -> {
      outgoingClazz.reset();
    });

    this.getClazzCommunications().clear();
  }

}
