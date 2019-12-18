package net.explorviz.security.server.resources.test.helper;

import com.github.jasminb.jsonapi.DeserializationFeature;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import net.explorviz.security.model.UserBatchRequest;
import net.explorviz.security.user.User;
import net.explorviz.security.user.Role;

public class JsonAPIMapper<T> implements ObjectMapper {
  private final ResourceConverter converter;

  private final Class<T> cls;

  public JsonAPIMapper(final Class<T> cls) {
    this.cls = cls;
    this.converter = new ResourceConverter();
    this.converter.registerType(User.class);
    this.converter.registerType(UserBatchRequest.class);
    this.converter.registerType(Role.class);
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
