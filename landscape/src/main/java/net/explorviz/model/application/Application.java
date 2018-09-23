package net.explorviz.model.application;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.model.helper.BaseEntity;
import net.explorviz.model.helper.EProgrammingLanguage;
import net.explorviz.model.helper.ModelHelper;
import net.explorviz.model.landscape.Node;

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
  private List<Component> components = new ArrayList<Component>();

  @Relationship("databaseQueries")
  private List<DatabaseQuery> databaseQueries = new ArrayList<DatabaseQuery>();

  @Relationship("outgoingApplicationCommunications")
  private List<ApplicationCommunication> outgoingApplicationCommunications =
  new ArrayList<ApplicationCommunication>();

  @Relationship("aggregatedOutgoingClazzCommunications")
  private List<AggregatedClazzCommunication> aggregatedOutgoingClazzCommunications =
  new ArrayList<AggregatedClazzCommunication>();

  @Relationship("cumulatedClazzCommunications")
  private List<CumulatedClazzCommunication> cumulatedClazzCommunications =
  new ArrayList<CumulatedClazzCommunication>();

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public EProgrammingLanguage getProgrammingLanguage() {
    return programmingLanguage;
  }

  public void setProgrammingLanguage(final EProgrammingLanguage programmingLanguage) {
    this.programmingLanguage = programmingLanguage;
  }

  public long getLastUsage() {
    return lastUsage;
  }

  public void setLastUsage(final long lastUsage) {
    this.lastUsage = lastUsage;
  }

  public Node getParent() {
    return parent;
  }

  public void setParent(final Node parent) {
    this.parent = parent;
  }

  public List<Component> getComponents() {
    return components;
  }

  public void setComponents(final List<Component> components) {
    this.components = components;
  }

  public void setDatabaseQueries(final List<DatabaseQuery> databaseQueries) {
    this.databaseQueries = databaseQueries;
  }

  public List<DatabaseQuery> getDatabaseQueries() {
    return databaseQueries;
  }

  public List<ApplicationCommunication> getOutgoingApplicationCommunications() {
    return outgoingApplicationCommunications;
  }

  public void setOutgoingApplicationCommunications(
      final List<ApplicationCommunication> outgoingCommunications) {
    this.outgoingApplicationCommunications = outgoingCommunications;
  }

  public List<AggregatedClazzCommunication> getAggregatedOutgoingClazzCommunications() {
    return aggregatedOutgoingClazzCommunications;
  }

  public void setAggregatedOutgoingClazzCommunications(
      final List<AggregatedClazzCommunication> aggregatedOutgoingClazzCommunications) {
    this.aggregatedOutgoingClazzCommunications = aggregatedOutgoingClazzCommunications;
  }

  public List<CumulatedClazzCommunication> getCumulatedClazzCommunications() {
    return cumulatedClazzCommunications;
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
