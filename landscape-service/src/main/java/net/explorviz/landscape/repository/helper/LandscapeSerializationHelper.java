package net.explorviz.landscape.repository.helper;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import javax.inject.Inject;
import net.explorviz.landscape.model.landscape.Landscape;

/**
 * Helper class for de-/serializing landscapes from/to json api.
 *
 */
public class LandscapeSerializationHelper {

  private final ResourceConverter jsonApiConverter;

  @Inject
  public LandscapeSerializationHelper(final ResourceConverter jsonApiConverter) {
    this.jsonApiConverter = jsonApiConverter;
  }


  /**
   * Serializes a landscape to a json api string.
   *
   * @throws DocumentSerializationException if the landscape could not be parsed.
   */
  public String serialize(final Landscape l) throws DocumentSerializationException {
    final JSONAPIDocument<Landscape> landscapeDoc = new JSONAPIDocument<>(l);
    final byte[] landscapeBytes = this.jsonApiConverter.writeDocument(landscapeDoc);
    return new String(landscapeBytes);

  }

  /**
   * Deserializes a json-api string to a {@link Landscape} object.
   *
   * @param jsonApi the json api string representing a landscape
   * @return the landscape
   * @throws DocumentSerializationException if the given string can't be deserialized to a landscape
   */
  public Landscape deserialize(final String jsonApi) throws DocumentSerializationException {

    final byte[] b = jsonApi.getBytes();
    final JSONAPIDocument<Landscape> landscapeDoc =
        this.jsonApiConverter.readDocument(b, Landscape.class);

    return landscapeDoc.get();
  }

}
