package net.explorviz.security.services;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.shared.security.model.roles.Role;
import org.jvnet.hk2.annotations.Service;

/**
 * Service to handle user roles.
 */
@Service
public class RoleService {

  /**
   * List of all roles.
   *
   * @return the list of all available roles
   */
  public List<Role> getAllRoles() {

    final List<Role> roleList = new ArrayList<>();

    roleList.add(new Role("admin"));
    roleList.add(new Role("user"));

    return roleList;
  }

}
