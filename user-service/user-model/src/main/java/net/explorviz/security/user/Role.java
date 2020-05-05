package net.explorviz.security.user;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The roles of a user reflect its privileges/permissions.
 */
@Type("role")
public class Role {

  /**
   * User's with this role have elevated access rights and may perform
   * administrative actions.
   */
  public static final String ADMIN_NAME = "admin";

  public static final Role ADMIN = new Role(ADMIN_NAME);

  /**
   * Restrictive role for basic users.
   */
  public static final String USER_NAME = "user";

  public static final Role USER = new Role(USER_NAME);

  public static final List<Role> ROLES = new ArrayList<>();

  static {
    ROLES.add(ADMIN);
    ROLES.add(USER);
  }

  @Id
  private String name;

  /**
   * Creates a new roles.
   */
  private Role(final String name) {
    this.name = name;
  }


  public Role() {/* Jackson */ }



  /**
   * Checks whether a role with the given name exists.
   *
   * @param roleName Name of the role to check
   * @return {@code true} iff a role with the given name exists
   */
  public static boolean exists(final String roleName) {
    return ROLES.stream().anyMatch(r -> r.getName().equals(roleName));
  }



  public String getName() {
    return name;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final Role role = (Role) o;

    return new EqualsBuilder().append(name, role.name).isEquals();
  }

  @Override
  public int hashCode() {
    final int initialOddNum = 17;
    final int mutliplier = 37;
    return new HashCodeBuilder(initialOddNum, mutliplier).append(name).toHashCode();
  }
}
