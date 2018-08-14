package net.explorviz.model.helper;

import java.util.Collections;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import com.github.jasminb.jsonapi.models.errors.Error;

public final class ErrorObjectHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorObjectHelper.class);

	private final ResourceConverter converter;

	@Inject
	public ErrorObjectHelper(final ResourceConverter converter) {
		this.converter = converter;
	}

	public String createErrorObjectString(final String errorTitle, final String errorDetail) {
		return createErrorObjectString(0, errorTitle, errorDetail);
	}

	public String createErrorObjectString(final int httpStatus, final String errorTitle, final String errorDetail) {

		final Error error = createErrorObject(httpStatus, errorTitle, errorDetail);

		final JSONAPIDocument<?> document = JSONAPIDocument.createErrorDocument(Collections.singleton(error));

		try {
			return new String(converter.writeDocument(document));
		} catch (final DocumentSerializationException e) {
			LOGGER.error("Error occured while converting ErrorObject. Error: {}", e.toString());

			String httpStatusEntry = "";
			if (httpStatus != 0) {
				httpStatusEntry = String.valueOf(httpStatus);
			}

			// Fallback string
			return "{\"errors\": [ \"status\": \"" + httpStatusEntry + "\", \"title\": \"" + errorTitle
					+ "\", \"detail\": \"" + errorDetail + "\"]}";

		}

	}

	public Error createErrorObject(final String errorTitle, final String errorDetail) {
		return createErrorObject(0, errorTitle, errorDetail);
	}

	public Error createErrorObject(final int httpStatus, final String errorTitle, final String errorDetail) {

		final Error error = new Error();

		if (httpStatus != 0) {
			error.setStatus(String.valueOf(httpStatus));
		}
		error.setTitle(errorTitle);
		error.setDetail(errorDetail);

		return error;
	}
}
