package net.explorviz.landscape.model.application;

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

  @Relationship("outgoingApplicationCommunications")
  private List<ApplicationCommunication> outgoingAppCommu = new ArrayList<>();

  @Relationship("aggregatedOutgoingClazzCommunications")
  private List<AggregatedClazzCommunication> aggregatedOutgoingClazzCommu = new ArrayList<>();

  @Relationship("cumulatedClazzCommunications")
  private List<CumulatedClazzCommunication> cumulatedClazzCommunications = new ArrayList<>();

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

  public List<ApplicationCommunication> getOutgoingApplicationCommunications() {
    return this.outgoingAppCommu;
  }

  public void setOutgoingApplicationCommunications(
      final List<ApplicationCommunication> outgoingCommunications) {
    this.outgoingAppCommu = outgoingCommunications;
  }

  public List<AggregatedClazzCommunication> getAggregatedOutgoingClazzCommunications() {
    return this.aggregatedOutgoingClazzCommu;
  }

  public void setAggregatedOutgoingClazzCommunications(
      final List<AggregatedClazzCommunication> aggregatedOutgoingClazzCommu) {
    this.aggregatedOutgoingClazzCommu = aggregatedOutgoingClazzCommu;
  }

  public List<CumulatedClazzCommunication> getCumulatedClazzCommunications() {
    return this.cumulatedClazzCommunications;
  }

  public void setCumulatedClazzCommunications(
      final List<CumulatedClazzCommunication> cumulatedClazzCommunications) {
    this.cumulatedClazzCommunications = cumulatedClazzCommunications;
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
    for (final ApplicationCommunication commu : this.getOutgoingApplicationCommunications()) {
      commu.reset();
    }

    // clears the aggregatedClazzCommunication
    this.getAggregatedOutgoingClazzCommunications().clear();

    // clears the cumulatedClazzCommunication
    this.getCumulatedClazzCommunications().clear();

    // clears database queries
    this.getDatabaseQueries().clear();
  }

}
