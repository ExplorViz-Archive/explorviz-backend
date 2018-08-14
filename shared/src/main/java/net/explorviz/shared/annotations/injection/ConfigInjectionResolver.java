package net.explorviz.shared.annotations.injection;

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

		System.out.println("type: " + injectee.getRequiredType());

		if (String.class == injectee.getRequiredType()) {
			return String.valueOf(handlePropertyLoading(injectee));
		}
		
		if (Integer.class == injectee.getRequiredType()) {
			return Integer.valueOf(String.valueOf(handlePropertyLoading(injectee)));
		}
		
		if (Boolean.class == injectee.getRequiredType()) {
			return Boolean.valueOf(String.valueOf(handlePropertyLoading(injectee)));
		}
		

		throw new InternalServerErrorException("Type for property injection is not valid.");
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
