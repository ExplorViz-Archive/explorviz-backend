package net.explorviz.security.services;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.shared.security.model.roles.Role;
import org.jvnet.hk2.annotations.Service;

@Service
public class RoleService {

  public List<Role> getAllRoles() {

    final List<Role> roleList = new ArrayList<>();

    roleList.add(new Role("admin"));
    roleList.add(new Role("user"));

    return roleList;
  }

}
