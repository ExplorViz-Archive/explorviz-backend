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
 * Model representing unidirectional communication between classes (from sourceClazz to targetClazz)
 * within a single application.
 */
@SuppressWarnings("serial")
@Type("aggregatedclazzcommunication")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "super.id")
public class AggregatedClazzCommunication extends BaseEntity {

  private int totalRequests;

  @Relationship("sourceClazz")
  private Clazz sourceClazz;

  @Relationship("targetClazz")
  private Clazz targetClazz;

  @Relationship("clazzCommunications")
  private List<ClazzCommunication> clazzCommunications = new ArrayList<>();

  private float averageResponseTime;

  @JsonCreator
  public AggregatedClazzCommunication(@JsonProperty("id") final String id) {
    super(id);
  }

  public int getTotalRequests() {
    return this.totalRequests;
  }

  public void setTotalRequests(final int requests) {
    this.totalRequests = requests;
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

  public List<ClazzCommunication> getClazzCommunications() {
    return this.clazzCommunications;
  }

  public void setClazzCommunications(final List<ClazzCommunication> clazzCommunications) {
    this.clazzCommunications = clazzCommunications;
  }

  public float getAverageResponseTime() {
    return this.averageResponseTime;
  }

  public void setAverageResponseTime(final float averageResponseTime) {
    this.averageResponseTime = averageResponseTime;
  }

  // adds a clazzCommunication if sourceClazz and targetClazz matches
  public boolean addClazzCommunication(final ClazzCommunication clazzcommunication) {

    if (this.sourceClazz.equals(clazzcommunication.getSourceClazz())
        && this.targetClazz.equals(clazzcommunication.getTargetClazz())) {
      this.setTotalRequests(this.getTotalRequests() + clazzcommunication.getTotalRequests());
      this.clazzCommunications.add(clazzcommunication);
      return true;
    }
    return false;
  }

  public void reset() {
    this.totalRequests = 0;
    this.clazzCommunications.clear();
    this.averageResponseTime = 0;
  }

}
