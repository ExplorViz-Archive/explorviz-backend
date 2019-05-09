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
import net.explorviz.settings.model.UserPreference;
import xyz.morphia.Datastore;

public class UserSettingJsonApiDeserializer implements MessageBodyReader<UserPreference> {

  private final ResourceConverter converter;

  @Inject
  public UserSettingJsonApiDeserializer(final ResourceConverter converter,
      final Datastore datastore) {
    this.converter = converter;
  }

  @Override
  public boolean isReadable(final Class<?> type, final Type genericType,
      final Annotation[] annotations, final MediaType mediaType) {
    // TODO Auto-generated method stub
    return true;
  }


  @Override
  public UserPreference readFrom(final Class<UserPreference> type, final Type genericType,
      final Annotation[] annotations, final MediaType mediaType,
      final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream)
      throws IOException, WebApplicationException {

    final UserPreference setting = this.converter.readDocument(entityStream, type).get();
    return setting;
  }

}
