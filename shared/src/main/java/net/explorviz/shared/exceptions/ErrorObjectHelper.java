package net.explorviz.shared.exceptions;

public final class ErrorObjectHelper {

	public String createErrorObjectString(final String errorTitle, final String errorDetail) {
		return createErrorObjectString(0, errorTitle, errorDetail);
	}

	public String createErrorObjectString(final int httpStatus, final String errorTitle, final String errorDetail) {

		String httpStatusEntry = "500";
		if (httpStatus != 0) {
			httpStatusEntry = String.valueOf(httpStatus);
		}
		
		return "{\"errors\": [ { \"status\": \"" + httpStatusEntry + "\", \"title\": \"" + errorTitle
				+ "\", \"detail\": \"" + errorDetail + "\" } ]}";

	}
}
