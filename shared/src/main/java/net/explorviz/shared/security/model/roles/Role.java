package net.explorviz.shared.security.model.roles;

import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Type;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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

  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Role)) {
      return false;
    }

    final Role other = (Role) obj;

    return new EqualsBuilder().append(this.id, other.getId())
        .append(this.descriptor, other.getDescriptor()).build();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(this.id).append(this.descriptor).build();
  }

  @Override
  public String toString() {
    return "{" + this.id + ", " + this.descriptor + "}";
  }



}
