package net.explorviz.security.server.resources.test.helper;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import java.util.List;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.security.user.User;
import net.explorviz.security.user.Role;

public class JsonAPIListMapper<T> implements ObjectMapper {
  private final ResourceConverter converter;

  private final Class<T> cls;

  public JsonAPIListMapper(final Class<T> cls) {
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
