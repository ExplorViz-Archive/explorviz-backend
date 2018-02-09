package net.explorviz.model.helper;

import java.util.Collections;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import com.github.jasminb.jsonapi.models.errors.Error;

public class ErrorObjectHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorObjectHelper.class);

	private final ResourceConverter converter;

	@Inject
	public ErrorObjectHelper(final ResourceConverter converter) {
		this.converter = converter;
	}

	public String createErrorObjectString(final int httpStatus, final String errorTitle, final String errorDetail) {

		final Error error = new Error();
		error.setStatus(String.valueOf(httpStatus));
		error.setTitle(errorTitle);
		error.setDetail(errorDetail);

		final JSONAPIDocument<?> document = JSONAPIDocument.createErrorDocument(Collections.singleton(error));

		try {
			return new String(converter.writeDocument(document));
		} catch (final DocumentSerializationException e) {
			LOGGER.error("Error occured while converting ErrorObject. Error: {}", e.toString());

			// Fallback string
			return "{\"errors\": [ \"status\": \"" + httpStatus + "\", \"title\": \"" + errorTitle
					+ "\", \"detail\": \"" + errorDetail + "\"]}";

		}

	}

}
