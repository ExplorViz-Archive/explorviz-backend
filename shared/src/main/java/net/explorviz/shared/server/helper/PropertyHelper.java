package net.explorviz.shared.server.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import javax.ws.rs.InternalServerErrorException;


/**
 * Utility class to read configuration properties. Use the {@link Config} instead.
 */
public final class PropertyHelper {

  private static final String PROPERTIES_FILENAME = "explorviz.properties";
  private static final String PROPERTIES_PATH;

  private static final Properties PROP = new Properties();

  private static final String ERROR_MESSAGE =
      "Couldn't load properties file. Is WEB-INF/explorviz.properties a valid file?";

  private PropertyHelper() {
    // don't instantiate
  }

  static {
    final ClassLoader loader = Thread.currentThread().getContextClassLoader();

    final URL urlToProperties = loader.getResource(PROPERTIES_FILENAME);

    if (urlToProperties == null) {
      throw new InternalServerErrorException(ERROR_MESSAGE);
    }

    PROPERTIES_PATH = loader.getResource(PROPERTIES_FILENAME).getFile();

    try {
      PROP.load(loader.getResourceAsStream(PROPERTIES_FILENAME));
    } catch (final IOException e) {
      throw new InternalServerErrorException(ERROR_MESSAGE, e);
    }
  }

  public static int getIntegerProperty(final String propName) {
    return Integer.parseInt(getStringProperty(propName));
  }

  public static String getStringProperty(final String propName) {
    return String.valueOf(PROP.get(propName));
  }

  public static boolean getBooleanProperty(final String propName) {
    return Boolean.parseBoolean(getStringProperty(propName));
  }

  /**
   * Set a boolean property inside the explorviz.properties file.
   *
   * @param propName - Property key
   * @param value - Property value. String or string-castable object.
   * @throws FileNotFoundException - Thrown if explorviz.properties was not found.
   * @throws IOException - Thrown if cast to String did not work.
   */
  public static void setBooleanProperty(final String propName, final boolean value)
      throws FileNotFoundException, IOException {
    PROP.setProperty(propName, String.valueOf(value));

    try (OutputStream out = Files.newOutputStream(Paths.get(PROPERTIES_PATH))) {
      PROP.store(out, null);
    }
  }

}
