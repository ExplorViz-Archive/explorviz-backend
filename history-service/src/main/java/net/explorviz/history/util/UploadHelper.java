package net.explorviz.history.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that contains helper methods for uploading landscapes.
 */
public final class UploadHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(UploadHelper.class);
  private static final int BUFFER_SIZE = 1024;

  private UploadHelper() {
    //Utility Class
  }

  /**
   * Remove the extension of a passed filename.
   *
   * @param fileName the fileName
   * @return the trimmed fileName
   */
  public static String removeFileNameExtension(final String fileName) {
    final int extPos = fileName.lastIndexOf('.');
    if (extPos == -1) {
      return fileName;
    } else {
      return fileName.substring(0, extPos);
    }
  }

  /**
   * Converts an InputStream to a string for further processing.
   *
   * @param is the inputStream
   * @return the resulted string
   */
  public static String convertInputstreamToString(final InputStream is) {
    byte[] inputByteArray;


    try {
      inputByteArray = IOUtils.toByteArray(is);
      final InputStream inputStream = new ByteArrayInputStream(inputByteArray);

      final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int bytesRead;
      final byte[] data = new byte[BUFFER_SIZE];
      while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, bytesRead);
      }

      buffer.flush();
      final byte[] outputByteArray = buffer.toByteArray();

      return new String(outputByteArray, StandardCharsets.UTF_8);
    } catch (final IOException e) {

      LOGGER.error(
          "Could not convert inputstream due to an I/O exception landscape with message {}",
          e.getMessage());
    }
    return null;

  }

}
