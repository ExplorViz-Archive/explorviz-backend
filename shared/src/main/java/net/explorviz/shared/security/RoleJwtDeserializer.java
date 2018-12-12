package net.explorviz.shared.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.shared.security.model.roles.Role;

@SuppressWarnings("serial")
public class RoleJwtDeserializer extends StdDeserializer<List<Role>> {

  public RoleJwtDeserializer() {
    super(List.class);
  }

  @Override
  public List<Role> deserialize(final JsonParser p, final DeserializationContext ctxt)
      throws IOException {

    final List<Role> roleList = new ArrayList<>();
    final JsonNode node = p.getCodec().readTree(p);

    node.forEach((nodeElem) -> {
      final Role role = new Role(nodeElem.get("id").asLong(), nodeElem.get("descriptor").asText());
      roleList.add(role);
    });

    return roleList;
  }

}
