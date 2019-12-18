package net.explorviz.settings.server.resources.test.helper;

import com.github.jasminb.jsonapi.DeserializationFeature;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.security.user.User;

public class JsonAPIMapper<T> implements ObjectMapper {
  private final ResourceConverter converter;

  private final Class<T> cls;

  public JsonAPIMapper(final Class<T> cls) {
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
    final T deserialized =
        this.converter.readDocument(context.getDataToDeserialize().asByteArray(), this.cls).get();
    return deserialized;
  }

  @Override
  public String serialize(final ObjectMapperSerializationContext context) {
    final JSONAPIDocument<T> doc = new JSONAPIDocument<>(context.getObjectToSerializeAs(this.cls));
    try {
      final byte[] serialized = this.converter.writeDocument(doc);
      return new String(serialized);
    } catch (final DocumentSerializationException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String serializeRaw(final T object) {
    final JSONAPIDocument<T> doc = new JSONAPIDocument<>(object);
    try {
      final byte[] serialized = this.converter.writeDocument(doc);
      return new String(serialized);
    } catch (final DocumentSerializationException e) {
      e.printStackTrace();
    }
    return null;
  }
}
