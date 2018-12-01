package net.explorviz.shared.security.model.roles;

import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import xyz.morphia.annotations.Entity;

@Entity("roles")
public class Role {

  @Id(LongIdHandler.class)
  @xyz.morphia.annotations.Id
  private Long id;

  private String descriptor;

  public Role() {
    // For MongoDB
  }

  public Role(final Long id, final String descriptor) {
    this.id = id;
    this.descriptor = descriptor;
  }

  public Long getId() {
    return this.id;
  }

  public String getDescriptor() {
    return this.descriptor;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public void setDescriptor(final String descriptor) {
    this.descriptor = descriptor;
  }

}
