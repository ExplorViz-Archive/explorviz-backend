package net.explorviz.shared.exceptions;

/**
 * Injectable class that can be used to create a stringified JSON-API compliant error object.
 */
public final class ErrorObjectHelper {

  /**
   * Creates a stringified JSON-API compliant error object for the given parameters and with HTTP
   * status code 500.
   *
   * @param errorTitle - The short title of the error
   * @param errorDetail - A detailed description (!) without implementation details
   * @return The stringified JSON-API compliant error object
   */
  public String createErrorObjectString(final String errorTitle, final String errorDetail) {
    return createErrorObjectString(0, errorTitle, errorDetail);
  }

  /**
   * Creates a stringified JSON-API compliant error object for the given parameters. If http status
   * is 0 than the default 500 HTTP status will be used.
   *
   * @param httpStatus - The HTTP status code for this error
   * @param errorTitle - The short title of the error
   * @param errorDetail - A detailed description (!) without implementation details
   * @return The stringified JSON-API compliant error object
   */
  public String createErrorObjectString(final int httpStatus, final String errorTitle,
      final String errorDetail) {

    String httpStatusEntry = "500"; // NOPMD
    if (httpStatus != 0) {
      httpStatusEntry = String.valueOf(httpStatus);
    }

    return "{\"errors\": [ { \"status\": \"" + httpStatusEntry + "\", \"title\": \"" + errorTitle
        + "\", \"detail\": \"" + errorDetail + "\" } ]}";

  }
}
