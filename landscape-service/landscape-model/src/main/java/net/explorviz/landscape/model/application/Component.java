package net.explorviz.landscape.model.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing a component (e.g., a package within a java application).
 */
@SuppressWarnings("serial")
@Type("component")
@JsonIgnoreProperties("parentApplication")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "super.id")
public class Component extends BaseEntity {

  private String name;
  private String fullQualifiedName;

  @Relationship("children")
  private List<Component> children = new ArrayList<>();

  @Relationship("clazzes")
  private List<Clazz> clazzes = new ArrayList<>();

  @Relationship("parentComponent")
  private Component parentComponent;

  // @Relationship("parentApplication")
  // Don't parse since cycle results in stackoverflow when accessing
  // latestLandscape
  @JsonIgnore
  private Application parentApplication;

  @JsonCreator
  public Component(@JsonProperty("id") final String id) {
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

  public void setFullQualifiedName(final String fullQualifiedName) {
    this.fullQualifiedName = fullQualifiedName;
  }

  public Component getParentComponent() {
    return this.parentComponent;
  }

  public void setParentComponent(final Component parentComponent) {
    this.parentComponent = parentComponent;
  }

  public Application getBelongingApplication() {
    return this.parentApplication;
  }

  public void setBelongingApplication(final Application belongingApplication) {
    this.parentApplication = belongingApplication;
  }

  public List<Component> getChildren() {
    return this.children;
  }

  public void setChildren(final List<Component> children) {
    this.children = children;
  }

  public List<Clazz> getClazzes() {
    return this.clazzes;
  }

  public void setClazzes(final List<Clazz> clazzes) {
    this.clazzes = clazzes;
  }
}
