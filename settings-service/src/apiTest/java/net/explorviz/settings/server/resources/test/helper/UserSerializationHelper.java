package net.explorviz.settings.server.resources.test.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import net.explorviz.security.user.User;

public class UserSerializationHelper {

  /**
   * Serializes a user WITH the password attribute, which otherwise is ignored by Jackson
   * 
   * @param u the user
   * @return a JSON:API string representing the user
   * @throws IOException if the json is invalid
   */
  public static String serialize(final User u) throws IOException {
    final String serialized = new JsonAPIMapper<>(User.class).serializeRaw(u);

    // Password is ignored we need to add it manually into the JSON tree
    final ObjectMapper mapper = new ObjectMapper();
    final JsonNode n = mapper.readTree(serialized);
    ((ObjectNode) n.at("/data/attributes")).put("password", u.getPassword());
    return n.toString();
  }

}
