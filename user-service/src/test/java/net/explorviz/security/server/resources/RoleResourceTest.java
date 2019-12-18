package net.explorviz.security.server.resources;


import static org.junit.jupiter.api.Assertions.assertTrue;
import net.explorviz.security.user.Role;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link RoleResource}. All tests are performed by just calling the methods of
 * {@link RoleResource}.
 */
class RoleResourceTest {

  private final RoleResource roleResource = new RoleResource();

  @Test
  public void rolesContainAdmin() {
    assertTrue(this.roleResource.getAllRoles().contains(Role.ADMIN),
        "Roles are missing 'admin' role.");
  }

  @Test
  public void rolesContainUser() {
    assertTrue(this.roleResource.getAllRoles().contains(Role.USER),
        "Roles are missing 'user' role.");
  }
}
