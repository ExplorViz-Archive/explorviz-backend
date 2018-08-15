package net.explorviz.shared.annotations.injection;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Properties;

import javax.inject.Singleton;
import javax.ws.rs.InternalServerErrorException;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.shared.annotations.Config;

/**
 * InjectionResolver for {@code @Config} annotation. You must bind it in your
 * implemented {@link org.glassfish.hk2.utilities.binding.AbstractBinder}},
 * e.g.:
 * 
 * <pre>
 * {@code this.bind(new ConfigInjectionResolver()).to(new TypeLiteral<InjectionResolver<Config>>() {});}
 * </pre>
 * 
 * @see net.explorviz.shared.annotations.Config
 */

@Singleton
public class ConfigInjectionResolver implements InjectionResolver<Config> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigInjectionResolver.class);

	private static final String PROPERTIES_FILENAME = "explorviz.properties";
	// private static String PROPERTIES_PATH;

	private static final Properties PROP = new Properties();

	/*
	 * private final Properties properties; public
	 * ConfigurationInjectionResolver(Properties properties) { this.properties =
	 * properties; }
	 */

	private final InternalServerErrorException exception = new InternalServerErrorException(
			"An internal server error occured. Contact your administrator.");

	public ConfigInjectionResolver() {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		// PROPERTIES_PATH = loader.getResource(PROPERTIES_FILENAME).getFile();

		try {
			PROP.load(loader.getResourceAsStream(PROPERTIES_FILENAME));
		} catch (final IOException e) {
			LOGGER.error(
					"Couldn't load properties file. Is WEB-INF/classes/explorviz.properties a valid file?. Exception: {}",
					e.getMessage());
			throw exception;
		}
	}

	@Override
	public Object resolve(Injectee injectee, ServiceHandle<?> root) {

		Type t = injectee.getRequiredType();

		if (String.class == t) {
			return handlePropertyLoading(injectee);
		}

		if ("int".equals(t.toString())) {
			try {
				return Integer.valueOf(handlePropertyLoading(injectee)).intValue();
			} catch (NumberFormatException e) {
				LOGGER.error("Property injection for type 'int' failed. Stacktrace:", e);
				throw exception;
			}
		}

		if ("boolean".equals(t.toString())) {
			return Boolean.valueOf(handlePropertyLoading(injectee)).booleanValue();
		}

		LOGGER.error("Property injection failed: {}",
				"Type '" + t + "' for property injection is not valid. Use String, int or boolean.");

		throw exception;

	}

	public String handlePropertyLoading(Injectee injectee) {
		Config annotation = injectee.getParent().getAnnotation(Config.class);

		if (annotation != null) {
			String propName = annotation.value();
			return String.valueOf(PROP.get(propName));
		}

		LOGGER.error("Property injection for type 'int' failed: {}",
				"Annotation for property injection is not present.");
		throw exception;
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
