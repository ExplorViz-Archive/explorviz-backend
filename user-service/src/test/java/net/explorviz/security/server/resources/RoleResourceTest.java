package net.explorviz.security.server.resources;


import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link RoleResource}. All tests are performed by just calling the methods of
 * {@link RoleResource}.
 */
class RoleResourceTest {

  private final RoleResource roleResource = new RoleResource();

  @Test
  public void rolesContainAdmin() {
    assertTrue(this.roleResource.getAllRoles().contains("admin"),
        "Roles are missing 'admin' role.");
  }

  @Test
  public void rolesContainUser() {
    assertTrue(this.roleResource.getAllRoles().contains("user"), "Roles are missing 'user' role.");
  }
}
