package net.explorviz.security.services;

import java.util.ArrayList;
import java.util.List;
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
  public List<String> getAllRoles() {

    final List<String> roleList = new ArrayList<>();

    roleList.add("admin");
    roleList.add("user");

    return roleList;
  }

}
