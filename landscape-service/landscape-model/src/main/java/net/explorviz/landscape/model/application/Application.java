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
import net.explorviz.landscape.model.helper.EProgrammingLanguage;
import net.explorviz.landscape.model.helper.ModelHelper;
import net.explorviz.landscape.model.landscape.Node;

/**
 * Model representing a single application with a software landscape.
 */
@SuppressWarnings("serial")
@Type("application")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "super.id")
public class Application extends BaseEntity {

  private String name;
  private EProgrammingLanguage programmingLanguage;
  private long lastUsage;

  @Relationship("parent")
  private Node parent;

  @Relationship("components")
  private List<Component> components = new ArrayList<>();

  @Relationship("databaseQueries")
  private List<DatabaseQuery> databaseQueries = new ArrayList<>();

  @Relationship("applicationCommunications")
  private List<ApplicationCommunication> applicationCommunications = new ArrayList<>();

  // all aggregated clazzCommunications within an application
  @Relationship("aggregatedClazzCommunications")
  private List<AggregatedClazzCommunication> aggregatedClazzCommunications = new ArrayList<>();

  // all tracaes within an application
  @Relationship("traces")
  private List<Trace> traces = new ArrayList<>();

  @JsonCreator
  public Application(@JsonProperty("id") final String id) {
    super(id);
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public EProgrammingLanguage getProgrammingLanguage() {
    return this.programmingLanguage;
  }

  public void setProgrammingLanguage(final EProgrammingLanguage programmingLanguage) {
    this.programmingLanguage = programmingLanguage;
  }

  public long getLastUsage() {
    return this.lastUsage;
  }

  public void setLastUsage(final long lastUsage) {
    this.lastUsage = lastUsage;
  }

  public Node getParent() {
    return this.parent;
  }

  public void setParent(final Node parent) {
    this.parent = parent;
  }

  public List<Component> getComponents() {
    return this.components;
  }

  public void setComponents(final List<Component> components) {
    this.components = components;
  }

  public void setDatabaseQueries(final List<DatabaseQuery> databaseQueries) {
    this.databaseQueries = databaseQueries;
  }

  public List<DatabaseQuery> getDatabaseQueries() {
    return this.databaseQueries;
  }

  public List<ApplicationCommunication> getApplicationCommunications() {
    return this.applicationCommunications;
  }

  public void setApplicationCommunications(
      final List<ApplicationCommunication> applicationCommunications) {
    this.applicationCommunications = applicationCommunications;
  }

  public List<AggregatedClazzCommunication> getAggregatedClazzCommunications() {
    return this.aggregatedClazzCommunications;
  }

  public void setAggregatedClazzCommunications(
      final List<AggregatedClazzCommunication> aggregatedClazzCommunications) {
    this.aggregatedClazzCommunications = aggregatedClazzCommunications;
  }

  public List<Trace> getTraces() {
    return this.traces;
  }

  public void setTraces(final List<Trace> traces) {
    this.traces = traces;
  }

  /**
   * Clears all existings communication from (landscape-perspective) and within the application
   * (application-perspective).
   */
  public void clearCommunication() {

    // reset clazzes (instance count zero) and clears clazzCommunications
    for (final Component component : this.getComponents()) {
      final List<Clazz> foundClazzes = ModelHelper.getChildrenComponentClazzes(component);
      for (final Clazz clazz : foundClazzes) {
        clazz.reset();
      }
    }

    // resets applicationCommunication
    for (final ApplicationCommunication commu : this.getApplicationCommunications()) {
      commu.reset();
    }

    // clears the clazzCommunications
    this.getAggregatedClazzCommunications().clear();

    // clear traces
    this.getTraces().clear();

    // clears database queries
    this.getDatabaseQueries().clear();
  }

}
