package net.explorviz.settings.server.resources.test.helper;

import com.github.jasminb.jsonapi.DeserializationFeature;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import net.explorviz.security.model.UserBatchRequest;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.shared.security.model.User;

public class JsonAPIMapper<T> implements ObjectMapper {
  private ResourceConverter converter;

  private Class<T> cls;

  public JsonAPIMapper(Class<T> cls) {
    this.cls = cls;
    converter = new ResourceConverter();
    converter.registerType(Setting.class);
    converter.registerType(RangeSetting.class);
    converter.registerType(FlagSetting.class);
    converter.registerType(User.class);
    converter.registerType(UserPreference.class);
    converter.disableDeserializationOption(DeserializationFeature.REQUIRE_RESOURCE_ID);
  }

  @Override public T deserialize(ObjectMapperDeserializationContext context) {
    T deserialized = converter.readDocument(context.getDataToDeserialize().asByteArray(), cls).get();
    return deserialized;
  }

  @Override public String serialize(ObjectMapperSerializationContext context) {
    JSONAPIDocument<T> doc = new JSONAPIDocument<>(context.getObjectToSerializeAs(cls));
    try {
      byte[] serialized = converter.writeDocument(doc);
      return new String(serialized);
    } catch (DocumentSerializationException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String serializeRaw(T object) {
    JSONAPIDocument<T> doc = new JSONAPIDocument<>(object);
    try {
      byte[] serialized = converter.writeDocument(doc);
      return new String(serialized);
    } catch (DocumentSerializationException e) {
      e.printStackTrace();
    }
    return null;
  }
}
