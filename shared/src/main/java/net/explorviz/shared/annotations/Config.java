package net.explorviz.shared.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use the {@code @Config} annotation to inject properties from the
 * explorviz.properties file. Supported types are String, int and boolean.
 * 
 * For example:
 * 
 * <pre>
 * {@code @Config("jwt.secret")}
 * private String secret;
 * </pre>
 * 
 * @see net.explorviz.shared.annotations.injection.ConfigInjectionResolver *
 * @param propertyKey Name of the desired property
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
	String value();
}