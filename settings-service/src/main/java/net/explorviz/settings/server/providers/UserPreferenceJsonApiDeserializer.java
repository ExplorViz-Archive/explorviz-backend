package net.explorviz.settings.server.providers;

import com.github.jasminb.jsonapi.ResourceConverter;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import net.explorviz.settings.model.UserPreference;

/**
 * {@link MessageBodyReader} which consumes JSON:API strings
 * and deserializes them into {@link UserPreference} entities.
 */
public class UserPreferenceJsonApiDeserializer implements MessageBodyReader<UserPreference> {

  private final ResourceConverter converter;

  @Inject
  public UserPreferenceJsonApiDeserializer(final ResourceConverter converter) {
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
                                 final MultivaluedMap<String, String> httpHeaders,
                                 final InputStream entityStream)
      throws WebApplicationException {

    return this.converter.readDocument(entityStream, type).get();
  }

}
