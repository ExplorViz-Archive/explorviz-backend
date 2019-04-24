package net.explorviz.settings.server.providers;

import com.github.jasminb.jsonapi.ResourceConverter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import net.explorviz.settings.server.resources.UserSettingsResource.SettingValue;

public class SettingValueDeserializer implements MessageBodyReader<SettingValue> {

  private final ResourceConverter converter;

  @Inject
  public SettingValueDeserializer(final ResourceConverter converter) {
    this.converter = converter;
  }

  @Override
  public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType) {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public SettingValue readFrom(Class<SettingValue> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
      throws IOException, WebApplicationException {
    // TODO Auto-generated method stub
    final SettingValue setting = this.converter.readDocument(entityStream, type).get();

    return setting;
  }

}
