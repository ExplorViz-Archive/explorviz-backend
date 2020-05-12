package net.explorviz.settings.server.resources.test.helper;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import java.util.List;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserPreference;

/**
 * Mapper for de/serialization of JSON:API collections into lists.
 * @param <T> the type to map
 */
public class JsonApiListMapper<T> implements ObjectMapper {
  private final ResourceConverter converter;

  private final Class<T> cls;

  /**
   * Creates a new mapper.
   * @param cls the class of the type this mapper is for.
   */
  public JsonApiListMapper(final Class<T> cls) {
    this.cls = cls;
    this.converter = new ResourceConverter();
    this.converter.registerType(Setting.class);
    this.converter.registerType(UserPreference.class);
    this.converter.registerType(RangeSetting.class);
    this.converter.registerType(FlagSetting.class);
    this.converter.registerType(UserPreference.class);
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
      throw new IllegalStateException("Could not deserialize", e);
    }
  }

}
