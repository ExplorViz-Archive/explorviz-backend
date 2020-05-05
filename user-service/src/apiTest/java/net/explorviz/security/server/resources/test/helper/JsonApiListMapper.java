package net.explorviz.security.server.resources.test.helper;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import java.util.List;
import net.explorviz.security.user.Role;
import net.explorviz.security.user.User;
import net.explorviz.settings.model.UserPreference;

/**
 * Custom mapper to de/serialize JSON:API collections to lists.
 * @param <T> Type to de/serialize
 */
public class JsonApiListMapper<T> implements ObjectMapper {
  private final ResourceConverter converter;

  private final Class<T> cls;

  /**
   * Creates a new mapper.
   * @param cls The class to de/serialize the list entries into.
   */
  public JsonApiListMapper(final Class<T> cls) {
    this.cls = cls;
    this.converter = new ResourceConverter();
    this.converter.registerType(User.class);
    this.converter.registerType(UserPreference.class);
    this.converter.registerType(Role.class);
  }

  @Override
  public List<T> deserialize(final ObjectMapperDeserializationContext context) {
    return this.converter
        .readDocumentCollection(context.getDataToDeserialize().asByteArray(), this.cls)
        .get();
  }

  @Override
  public String serialize(final ObjectMapperSerializationContext context) {
    final List<T> l = (List<T>) context.getObjectToSerialize();
    final JSONAPIDocument<List<T>> doc = new JSONAPIDocument<>(l);
    try {
      final byte[] serialized = this.converter.writeDocumentCollection(doc);
      return new String(serialized);
    } catch (final DocumentSerializationException e) {
      e.printStackTrace();
    }
    return null;
  }

}
