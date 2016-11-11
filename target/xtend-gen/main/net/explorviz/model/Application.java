package net.explorviz.model;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.model.Communication;
import net.explorviz.model.CommunicationClazz;
import net.explorviz.model.Component;
import net.explorviz.model.DatabaseQuery;
import net.explorviz.model.Node;
import net.explorviz.model.helper.CommunicationAppAccumulator;
import net.explorviz.model.helper.DrawNodeEntity;
import net.explorviz.model.helper.ELanguage;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class Application extends DrawNodeEntity {
  @Accessors
  private int id;
  
  @Accessors
  private boolean database;
  
  @Accessors
  private ELanguage programmingLanguage;
  
  @Accessors
  private long lastUsage;
  
  @Accessors
  private Node parent;
  
  @Accessors
  private List<Component> components = new ArrayList<Component>();
  
  @Accessors
  private List<CommunicationClazz> communications = new ArrayList<CommunicationClazz>();
  
  @Accessors
  private final transient List<CommunicationAppAccumulator> communicationsAccumulated = new ArrayList<CommunicationAppAccumulator>();
  
  @Accessors
  private List<Communication> incomingCommunications = new ArrayList<Communication>();
  
  @Accessors
  private List<Communication> outgoingCommunications = new ArrayList<Communication>();
  
  @Accessors
  private List<DatabaseQuery> databaseQueries = new ArrayList<DatabaseQuery>();
  
  public void clearAllPrimitiveObjects() {
    for (final Component component : this.components) {
      component.clearAllPrimitiveObjects();
    }
  }
  
  public void unhighlight() {
    for (final Component component : this.components) {
      component.unhighlight();
    }
  }
  
  public void openAllComponents() {
    for (final Component component : this.components) {
      component.openAllComponents();
    }
  }
  
  public void closeAllComponents() {
    for (final Component component : this.components) {
      component.closeAllComponents();
    }
  }
  
  @Pure
  public int getId() {
    return this.id;
  }
  
  public void setId(final int id) {
    this.id = id;
  }
  
  @Pure
  public boolean isDatabase() {
    return this.database;
  }
  
  public void setDatabase(final boolean database) {
    this.database = database;
  }
  
  @Pure
  public ELanguage getProgrammingLanguage() {
    return this.programmingLanguage;
  }
  
  public void setProgrammingLanguage(final ELanguage programmingLanguage) {
    this.programmingLanguage = programmingLanguage;
  }
  
  @Pure
  public long getLastUsage() {
    return this.lastUsage;
  }
  
  public void setLastUsage(final long lastUsage) {
    this.lastUsage = lastUsage;
  }
  
  @Pure
  public Node getParent() {
    return this.parent;
  }
  
  public void setParent(final Node parent) {
    this.parent = parent;
  }
  
  @Pure
  public List<Component> getComponents() {
    return this.components;
  }
  
  public void setComponents(final List<Component> components) {
    this.components = components;
  }
  
  @Pure
  public List<CommunicationClazz> getCommunications() {
    return this.communications;
  }
  
  public void setCommunications(final List<CommunicationClazz> communications) {
    this.communications = communications;
  }
  
  @Pure
  public List<CommunicationAppAccumulator> getCommunicationsAccumulated() {
    return this.communicationsAccumulated;
  }
  
  @Pure
  public List<Communication> getIncomingCommunications() {
    return this.incomingCommunications;
  }
  
  public void setIncomingCommunications(final List<Communication> incomingCommunications) {
    this.incomingCommunications = incomingCommunications;
  }
  
  @Pure
  public List<Communication> getOutgoingCommunications() {
    return this.outgoingCommunications;
  }
  
  public void setOutgoingCommunications(final List<Communication> outgoingCommunications) {
    this.outgoingCommunications = outgoingCommunications;
  }
  
  @Pure
  public List<DatabaseQuery> getDatabaseQueries() {
    return this.databaseQueries;
  }
  
  public void setDatabaseQueries(final List<DatabaseQuery> databaseQueries) {
    this.databaseQueries = databaseQueries;
  }
}
