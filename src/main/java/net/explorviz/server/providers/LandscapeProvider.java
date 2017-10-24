package net.explorviz.server.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;

import net.explorviz.model.Landscape;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LandscapeProvider implements MessageBodyReader<Landscape>, MessageBodyWriter<Landscape> {

	final ResourceConverter converter;

	@Inject
	public LandscapeProvider(ResourceConverter converter) {
		this.converter = converter;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void writeTo(Landscape t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {

		JSONAPIDocument<Landscape> document = new JSONAPIDocument<Landscape>(t);

		try {
			entityStream.write(this.converter.writeDocument(document));
		} catch (DocumentSerializationException e) {
			System.err.println("Error when serializing Landscape: " + e);
			e.printStackTrace();
		} finally {
			entityStream.flush();
			entityStream.close();
		}

	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Landscape readFrom(Class<Landscape> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		return this.converter.readDocument(entityStream, type).get();
	}

}
