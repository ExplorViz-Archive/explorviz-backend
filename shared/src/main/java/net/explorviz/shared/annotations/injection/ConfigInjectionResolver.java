package net.explorviz.shared.annotations.injection;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Properties;
import javax.inject.Singleton;
import javax.ws.rs.InternalServerErrorException;
import net.explorviz.shared.annotations.Config;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InjectionResolver for {@code @Config} annotation. You must bind it in your implemented
 * {@link org.glassfish.hk2.utilities.binding.AbstractBinder}}, e.g.:
 *
 * <pre>
 * {@code this.bind(new ConfigInjectionResolver())
 * .to(new TypeLiteral<InjectionResolver<Config>>() {});}
 * </pre>
 *
 * @see net.explorviz.shared.annotations.Config
 */

@Singleton
public class ConfigInjectionResolver implements InjectionResolver<Config> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigInjectionResolver.class);

  private static final String PROPERTIES_DEFAULT_FILENAME = "explorviz.properties";
  private static final String PROPERTIES_CUSTOM_FILENAME = "explorviz-custom.properties";


  private static final Properties PROP = new Properties();

  private final InternalServerErrorException exception = new InternalServerErrorException(
      "An internal server error occured. Contact your administrator.");

  /**
   * Creates a ConfigInjectionResolver that is used to load injectable configuration properties from
   * the explorviz.properties file. Will be automatically created and registered in the CDI context
   * at application startup.
   */
  public ConfigInjectionResolver() {

    final ClassLoader loader = Thread.currentThread().getContextClassLoader();

    InputStream input = loader.getResourceAsStream(PROPERTIES_CUSTOM_FILENAME);

    if (input == null) {
      // use default properties
      input = loader.getResourceAsStream(PROPERTIES_DEFAULT_FILENAME);

      if (input == null) {
        LOGGER.error("Couldn't load default property file.");
        throw this.exception;
      }

      LOGGER.info("Using default property file.");
    } else {
      LOGGER.info("Using custom property file.");
    }

    try {
      PROP.load(input);
    } catch (final IOException e) {
      LOGGER.error("Couldn't load property file.");
      throw this.exception;
    }
  }

  @Override
  public Object resolve(final Injectee injectee, final ServiceHandle<?> root) {

    final Type t = injectee.getRequiredType();

    if (String.class == t) {
      return this.handlePropertyLoading(injectee);
    }

    if ("int".equals(t.toString())) {
      try {
        return Integer.valueOf(this.handlePropertyLoading(injectee));
      } catch (final NumberFormatException e) {
        LOGGER.error("Property injection for type 'int' failed. Stacktrace:", e);
        throw this.exception;
      }
    }

    if ("boolean".equals(t.toString())) {
      return Boolean.valueOf(this.handlePropertyLoading(injectee));
    }

    if (LOGGER.isErrorEnabled()) {
      LOGGER.error("Property injection failed: {}",
          "Type '" + t + "' for property injection is not valid. Use String, int or boolean.");
    }
    throw this.exception;

  }

  private String handlePropertyLoading(final Injectee injectee) {
    final Config annotation = injectee.getParent().getAnnotation(Config.class);

    if (annotation != null) {

      final String propName = annotation.value();
      return String.valueOf(PROP.get(propName));
    }

    LOGGER.error("Property injection for type 'String' failed: {}",
        "Annotation for property injection is not present.");
    throw this.exception;
  }

  @Override
  public boolean isConstructorParameterIndicator() {
    return true;
  }

  @Override
  public boolean isMethodParameterIndicator() {
    return true;
  }

}
