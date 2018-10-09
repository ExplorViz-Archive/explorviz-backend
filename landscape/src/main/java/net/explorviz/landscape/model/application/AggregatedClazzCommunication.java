package net.explorviz.landscape.model.application;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing aggregated communication between classes (from sourceClazz to targetClazz)
 * within a single application.
 */
@SuppressWarnings("serial")
@Type("aggregatedclazzcommunication")
public class AggregatedClazzCommunication extends BaseEntity {

  private int requests;

  @Relationship("sourceClazz")
  private Clazz sourceClazz;

  @Relationship("targetClazz")
  private Clazz targetClazz;

  @Relationship("outgoingClazzCommunications")
  private List<ClazzCommunication> outgoingClazzCommunications = new ArrayList<>();

  public int getRequests() {
    return this.requests;
  }

  public void setRequests(final int requests) {
    this.requests = requests;
  }

  public Clazz getSourceClazz() {
    return this.sourceClazz;
  }

  public void setSourceClazz(final Clazz sourceClazz) {
    this.sourceClazz = sourceClazz;
  }

  public Clazz getTargetClazz() {
    return this.targetClazz;
  }

  public void setTargetClazz(final Clazz targetClazz) {
    this.targetClazz = targetClazz;
  }

  public List<ClazzCommunication> getOutgoingClazzCommunications() {
    return this.outgoingClazzCommunications;
  }

  public void setOutgoingClazzCommunications(final List<ClazzCommunication> clazzCommunications) {
    this.outgoingClazzCommunications = clazzCommunications;
  }

  // adds a clazzCommunication if sourceClazz and targetClazz matches
  public boolean addClazzCommunication(final ClazzCommunication clazzcommunication) {

    if (this.sourceClazz.equals(clazzcommunication.getSourceClazz())
        && this.targetClazz.equals(clazzcommunication.getTargetClazz())) {
      this.setRequests(this.getRequests() + clazzcommunication.getRequests());
      this.outgoingClazzCommunications.add(clazzcommunication);
      return true;
    }
    return false;
  }

  public void reset() {
    this.requests = 0;
    this.outgoingClazzCommunications.clear();
  }

}
