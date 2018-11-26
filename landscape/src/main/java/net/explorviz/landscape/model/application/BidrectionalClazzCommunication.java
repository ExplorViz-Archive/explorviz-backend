package net.explorviz.landscape.model.application;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing bidirectional communications between classes (both directions) (within a
 * single application).
 */

@SuppressWarnings("serial")
@Type("bidrectionalclazzcommunication")
public class BidrectionalClazzCommunication extends BaseEntity {

  private int requests;

  @Relationship("sourceClazz")
  private Clazz sourceClazz;

  @Relationship("targetClazz")
  private Clazz targetClazz;

  @Relationship("unidirectionalClazzCommunications")
  private List<UnidirectionalClazzCommunication> unidirectionalClazzCommunications =
      new ArrayList<>();

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

  public List<UnidirectionalClazzCommunication> getUnidirectionalClazzCommunications() {
    return this.unidirectionalClazzCommunications;
  }

  public void setunidirectionalClazzCommunications(
      final List<UnidirectionalClazzCommunication> clazzCommunications) {
    this.unidirectionalClazzCommunications = clazzCommunications;
  }

  // adds a clazzCommunication if sourceClazz and targetClazz matches or otherwise
  public boolean addUnidirectionalClazzCommunication(
      final UnidirectionalClazzCommunication aggClazzcommunication) {

    if (this.sourceClazz.equals(aggClazzcommunication.getSourceClazz())
        && this.targetClazz.equals(aggClazzcommunication.getTargetClazz())
        || this.sourceClazz.equals(aggClazzcommunication.getTargetClazz())
            && this.targetClazz.equals(aggClazzcommunication.getSourceClazz())) {
      this.setRequests(this.getRequests() + aggClazzcommunication.getRequests());
      this.unidirectionalClazzCommunications.add(aggClazzcommunication);
      return true;
    }
    return false;
  }

  public void reset() {
    this.requests = 0;
    this.unidirectionalClazzCommunications.clear();
  }

}
