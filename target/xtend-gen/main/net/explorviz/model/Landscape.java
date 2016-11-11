package net.explorviz.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.explorviz.model.Communication;
import net.explorviz.model.helper.BaseEntity;
import net.explorviz.model.helper.CommunicationAccumulator;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class Landscape extends BaseEntity {
  @Accessors
  private long hash;
  
  @Accessors
  private long activities;
  
  @Accessors
  private List<net.explorviz.model.System> systems = new ArrayList<net.explorviz.model.System>();
  
  @Accessors
  private List<Communication> applicationCommunication = new ArrayList<Communication>();
  
  @Accessors
  private Map<Long, String> events = new TreeMap<Long, String>();
  
  @Accessors
  private Map<Long, String> errors = new TreeMap<Long, String>();
  
  @Accessors
  private final transient List<CommunicationAccumulator> communicationsAccumulated = new ArrayList<CommunicationAccumulator>(
    4);
  
  public void updateLandscapeAccess(final long timeInNano) {
    this.setHash(timeInNano);
  }
  
  public void destroy() {
  }
  
  @Pure
  public long getHash() {
    return this.hash;
  }
  
  public void setHash(final long hash) {
    this.hash = hash;
  }
  
  @Pure
  public long getActivities() {
    return this.activities;
  }
  
  public void setActivities(final long activities) {
    this.activities = activities;
  }
  
  @Pure
  public List<net.explorviz.model.System> getSystems() {
    return this.systems;
  }
  
  public void setSystems(final List<net.explorviz.model.System> systems) {
    this.systems = systems;
  }
  
  @Pure
  public List<Communication> getApplicationCommunication() {
    return this.applicationCommunication;
  }
  
  public void setApplicationCommunication(final List<Communication> applicationCommunication) {
    this.applicationCommunication = applicationCommunication;
  }
  
  @Pure
  public Map<Long, String> getEvents() {
    return this.events;
  }
  
  public void setEvents(final Map<Long, String> events) {
    this.events = events;
  }
  
  @Pure
  public Map<Long, String> getErrors() {
    return this.errors;
  }
  
  public void setErrors(final Map<Long, String> errors) {
    this.errors = errors;
  }
  
  @Pure
  public List<CommunicationAccumulator> getCommunicationsAccumulated() {
    return this.communicationsAccumulated;
  }
}
