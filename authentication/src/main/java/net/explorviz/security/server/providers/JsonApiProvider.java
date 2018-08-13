package net.explorviz.security.server.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

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

import net.explorviz.security.model.Token;
import net.explorviz.security.model.User;
import net.explorviz.security.model.UserCredentials;

@Provider
@Produces("application/vnd.api+json")
@Consumes("application/vnd.api+json")
public class JsonApiProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonApiProvider.class);

	private final ResourceConverter converter;

	public JsonApiProvider() {
		this.converter = new ResourceConverter(User.class, UserCredentials.class, Token.class);
	}

	@Override
	public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public long getSize(final T t, final Class<?> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeTo(final T t, final Class<?> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders,
			final OutputStream entityStream) throws IOException, WebApplicationException {
		final JSONAPIDocument<T> document = new JSONAPIDocument<>(t);

		try {
			entityStream.write(this.converter.writeDocument(document));
		} catch (final DocumentSerializationException e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("Error when serializing object of type" + t.getClass() + ": ", e);
			}
		} finally {
			entityStream.flush();
			entityStream.close();
		}

	}

	@Override
	public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public T readFrom(final Class<T> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream)
			throws IOException, WebApplicationException {
		return this.converter.readDocument(entityStream, type).get();
	}

}
