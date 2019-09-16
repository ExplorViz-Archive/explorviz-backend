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
import net.explorviz.shared.security.model.User;

public class JsonAPIListMapper<T> implements ObjectMapper {
  private ResourceConverter converter;

  private Class<T> cls;

  public JsonAPIListMapper(Class<T> cls) {
    this.cls = cls;
    converter = new ResourceConverter();
    converter.registerType(Setting.class);
    converter.registerType(UserPreference.class);
    converter.registerType(RangeSetting.class);
    converter.registerType(FlagSetting.class);
    converter.registerType(UserPreference.class);
  }

  @Override public List<T> deserialize(ObjectMapperDeserializationContext context) {
    return converter.readDocumentCollection(context.getDataToDeserialize().asByteArray(), cls).get();
  }

  @Override public String serialize(ObjectMapperSerializationContext context) {
    List<T> l = (List<T>) context.getObjectToSerialize();
    JSONAPIDocument<List<T>> doc = new JSONAPIDocument<List<T>>(l);
    try {
      byte[] serialized = converter.writeDocumentCollection(doc);
      return new String(serialized);
    } catch (DocumentSerializationException e) {
      e.printStackTrace();
    }
    return null;
  }

}
