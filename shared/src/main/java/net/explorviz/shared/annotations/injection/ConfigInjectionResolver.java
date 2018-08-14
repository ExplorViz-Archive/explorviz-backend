package net.explorviz.shared.annotations.injection;

import java.lang.reflect.Type;

import javax.inject.Singleton;
import javax.ws.rs.InternalServerErrorException;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;

import net.explorviz.shared.annotations.Config;

@Singleton
public class ConfigInjectionResolver implements InjectionResolver<Config> {

	/*
	 * private final Properties properties; public
	 * ConfigurationInjectionResolver(Properties properties) { this.properties =
	 * properties; }
	 */

	@Override
	public Object resolve(Injectee injectee, ServiceHandle<?> root) {

		Type t = injectee.getRequiredType();

		if (String.class == t) {
			return String.valueOf(handlePropertyLoading(injectee));
		}

		if ("int".equals(t.toString())) {
			return Integer.valueOf(String.valueOf(handlePropertyLoading(injectee))).intValue();
		}

		if ("boolean".equals(t.toString())) {
			return Boolean.valueOf(String.valueOf(handlePropertyLoading(injectee))).booleanValue();
		}

		throw new InternalServerErrorException(
				"Type '" + t + "' for property injection is not valid. Use String, int or boolean.");
	}

	public Object handlePropertyLoading(Injectee injectee) {
		Config annotation = injectee.getParent().getAnnotation(Config.class);
		if (annotation != null) {
			String prop = annotation.value();
			// return properties.getProperty(prop);
			return prop;
		}
		throw new InternalServerErrorException("Annotation for property injection is not present.");
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
