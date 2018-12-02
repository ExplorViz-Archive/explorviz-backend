package net.explorviz.shared.security.model.roles;

import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Type;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.IndexOptions;
import xyz.morphia.annotations.Indexed;

@Type("role")
@Entity("roles")
public class Role {

  @Id
  @com.github.jasminb.jsonapi.annotations.Id(LongIdHandler.class)
  private Long id;

  @Indexed(options = @IndexOptions(unique = true))
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
