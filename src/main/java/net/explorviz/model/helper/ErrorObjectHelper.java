package net.explorviz.model.helper;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import com.github.jasminb.jsonapi.models.errors.Error;

import net.explorviz.server.injection.ResourceConverterFactory;

public final class ErrorObjectHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorObjectHelper.class);

	private static final Object LOCK = new Object();

	private static ErrorObjectHelper instance;

	private final ResourceConverter converter;

	private ErrorObjectHelper() {
		this.converter = new ResourceConverterFactory().provide();
	}

	public static ErrorObjectHelper getInstance() {
		synchronized (LOCK) {
			if (ErrorObjectHelper.instance == null) {
				ErrorObjectHelper.instance = new ErrorObjectHelper();
			}
			return ErrorObjectHelper.instance;
		}
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
