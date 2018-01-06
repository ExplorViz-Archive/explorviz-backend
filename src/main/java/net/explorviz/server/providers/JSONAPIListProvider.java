package net.explorviz.server.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;

@Provider
@Produces("application/vnd.api+json")
@Consumes("application/vnd.api+json")
public class JSONAPIListProvider implements MessageBodyReader<List<?>>, MessageBodyWriter<List<?>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JSONAPIListProvider.class);

	private final ResourceConverter converter;

	@Inject
	public JSONAPIListProvider(final ResourceConverter converter) {
		this.converter = converter;
	}

	@Override
	public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public long getSize(final List<?> t, final Class<?> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeTo(final List<?> t, final Class<?> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders,
			final OutputStream entityStream) throws IOException, WebApplicationException {
		final JSONAPIDocument<List<?>> document = new JSONAPIDocument<List<?>>(t);

		try {
			entityStream.write(this.converter.writeDocumentCollection(document));
		} catch (final DocumentSerializationException e) {
			LOGGER.error("Error when serializing Process List: ", e);
		} finally {
			entityStream.flush();
			entityStream.close();
		}

	}

	@Override
	public List<?> readFrom(final Class<List<?>> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream)
			throws IOException, WebApplicationException {
		return this.converter.readDocumentCollection(entityStream, type).get();
	}

	@Override
	public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType) {
		// TODO Auto-generated method stub
		return true;
	}

}
