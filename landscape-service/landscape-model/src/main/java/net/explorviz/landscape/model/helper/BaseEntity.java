package net.explorviz.landscape.model.helper;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.annotations.Id;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Base Model for all other data model entities.
 */
@SuppressWarnings("serial")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "id")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class BaseEntity implements Serializable {

  /*
   * This attribute can be used by extensions to insert custom properties to any meta-model object.
   * Non primitive types (your custom model class) must be annotated with type annotations, e.g., as
   * shown in any model entity
   */
  private final Map<String, Object> extensionAttributes = new HashMap<>();

  @Id
  @JsonProperty("id")
  protected String id;

  public BaseEntity(final String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }

  @JsonSetter
  public void setId(final String id) {
    this.id = id;
  }

  public Map<String, Object> getExtensionAttributes() {
    return this.extensionAttributes;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(id).build();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BaseEntity other = (BaseEntity) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }



}
