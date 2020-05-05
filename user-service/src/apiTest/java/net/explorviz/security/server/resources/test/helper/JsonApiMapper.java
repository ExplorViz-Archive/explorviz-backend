package net.explorviz.security.server.resources.test.helper;

import com.github.jasminb.jsonapi.DeserializationFeature;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import net.explorviz.security.model.UserBatchRequest;
import net.explorviz.security.user.Role;
import net.explorviz.security.user.User;

/**
 * Custom mapper to de/serialize JSON:API.
 *
 * @param <T> Type to de/serialize
 */
public class JsonApiMapper<T> implements ObjectMapper {
  private final ResourceConverter converter;

  private final Class<T> cls;

  /**
   * Creates a new mapper.
   *
   * @param cls The class to de/serialize.
   */
  public JsonApiMapper(final Class<T> cls) {
    this.cls = cls;
    this.converter = new ResourceConverter();
    this.converter.registerType(User.class);
    this.converter.registerType(UserBatchRequest.class);
    this.converter.registerType(Role.class);
    this.converter.disableDeserializationOption(DeserializationFeature.REQUIRE_RESOURCE_ID);
  }

  @Override
  public T deserialize(final ObjectMapperDeserializationContext context) {
    return this.converter.readDocument(context.getDataToDeserialize().asByteArray(), this.cls)
        .get();
  }

  @Override
  public String serialize(final ObjectMapperSerializationContext context) {
    final JSONAPIDocument<T> doc = new JSONAPIDocument<>(context.getObjectToSerializeAs(this.cls));
    try {
      final byte[] serialized = this.converter.writeDocument(doc);
      return new String(serialized);
    } catch (final DocumentSerializationException ignored) {
    }
    return null;
  }

  /**
   * Wrapper to serialize an object.
   *
   * @param object the object to serialize
   * @return JSON:API string representation of the given object
   */
  public String serializeRaw(final T object) {
    final JSONAPIDocument<T> doc = new JSONAPIDocument<>(object);
    try {
      final byte[] serialized = this.converter.writeDocument(doc);
      return new String(serialized);
    } catch (final DocumentSerializationException ignored) {
    }
    return null;
  }
}
