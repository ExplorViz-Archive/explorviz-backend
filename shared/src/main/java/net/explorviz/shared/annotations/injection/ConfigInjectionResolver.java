package net.explorviz.shared.annotations.injection;

import java.lang.reflect.Type;

import javax.inject.Singleton;
import javax.ws.rs.InternalServerErrorException;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.shared.annotations.Config;

@Singleton
public class ConfigInjectionResolver implements InjectionResolver<Config> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigInjectionResolver.class);

	/*
	 * private final Properties properties; public
	 * ConfigurationInjectionResolver(Properties properties) { this.properties =
	 * properties; }
	 */

	private final InternalServerErrorException exception = new InternalServerErrorException(
			"An internal server error occured. Contact your administrator.");

	@Override
	public Object resolve(Injectee injectee, ServiceHandle<?> root) {

		Type t = injectee.getRequiredType();

		if (String.class == t) {
			return String.valueOf(handlePropertyLoading(injectee));
		}

		if ("int".equals(t.toString())) {
			try {
				return Integer.valueOf(String.valueOf(handlePropertyLoading(injectee))).intValue();
			} catch (NumberFormatException e) {
				LOGGER.error("Property injection for type 'int' failed. Stacktrace:", e);
				throw exception;
			}
		}

		if ("boolean".equals(t.toString())) {
			return Boolean.valueOf(String.valueOf(handlePropertyLoading(injectee))).booleanValue();
		}

		LOGGER.error("Property injection failed: {}",
				"Type '" + t + "' for property injection is not valid. Use String, int or boolean.");
		throw exception;

	}

	public Object handlePropertyLoading(Injectee injectee) {
		Config annotation = injectee.getParent().getAnnotation(Config.class);
		if (annotation != null) {
			String prop = annotation.value();
			// return properties.getProperty(prop);
			return prop;
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
