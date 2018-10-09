package net.explorviz.landscape.model.helper;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Base Model for all other data model entities.
 */
@SuppressWarnings("serial")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "id")
public class BaseEntity implements Serializable {

  private static final AtomicLong ID_GENERATOR = new AtomicLong();

  /*
   * This attribute can be used by extensions to insert custom properties to any meta-model object.
   * Non primitive types (your custom model class) must be annotated with type annotations, e.g., as
   * shown in any model entity
   */
  private final Map<String, Object> extensionAttributes = new HashMap<>();

  @Id(LongIdHandler.class)
  private Long id;

  public BaseEntity() {
    this.id = Long.valueOf(BaseEntity.ID_GENERATOR.incrementAndGet());
  }

  public Long getId() {
    return this.id;
  }

  public void initializeId() {
    // this.id = Long.valueOf(BaseEntity.ID_GENERATOR.incrementAndGet());
  }

  public void updateId() {
    this.id = Long.valueOf(BaseEntity.ID_GENERATOR.incrementAndGet());
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Map<String, Object> getExtensionAttributes() {
    return this.extensionAttributes;
  }

}
