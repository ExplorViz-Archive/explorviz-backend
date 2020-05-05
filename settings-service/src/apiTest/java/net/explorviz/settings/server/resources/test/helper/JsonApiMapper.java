package net.explorviz.settings.server.resources.test.helper;

import com.github.jasminb.jsonapi.DeserializationFeature;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import net.explorviz.security.user.User;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserPreference;

/**
 * Mapper to de/serialize JSON:API into objects.
 * @param <T> The type
 */
public class JsonApiMapper<T> implements ObjectMapper {
  private final ResourceConverter converter;

  private final Class<T> cls;

  /**
   * Creates a new mapper.
   * @param cls the type to map
   */
  public JsonApiMapper(final Class<T> cls) {
    this.cls = cls;
    this.converter = new ResourceConverter();
    this.converter.registerType(Setting.class);
    this.converter.registerType(RangeSetting.class);
    this.converter.registerType(FlagSetting.class);
    this.converter.registerType(User.class);
    this.converter.registerType(UserPreference.class);
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
    } catch (final DocumentSerializationException e) {
      throw new IllegalStateException("Could not serialize", e);
    }
  }

  /**
   * Serializes an object.
   * @param object the object
   * @return JSON:API string representation of the object
   */
  public String serializeRaw(final T object) {
    final JSONAPIDocument<T> doc = new JSONAPIDocument<>(object);
    try {
      final byte[] serialized = this.converter.writeDocument(doc);
      return new String(serialized);
    } catch (final DocumentSerializationException e) {
      throw new IllegalStateException("Could not deserialize", e);
    }
  }
}
