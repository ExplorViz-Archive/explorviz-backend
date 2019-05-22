package net.explorviz.common.live_trace_processing.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public final class ConfigurationFactory implements Keys {

	private ConfigurationFactory() {
	}

	public static final Configuration createSingletonConfiguration() {
		final Configuration defaultConfiguration = ConfigurationFactory
				.defaultConfiguration();

		// Searching for configuration file location passed to JVM
		String configurationFile = System
				.getProperty(Keys.CUSTOM_PROPERTIES_LOCATION_JVM);
		final Configuration loadConfiguration;
		if (configurationFile != null) {
			loadConfiguration = ConfigurationFactory.loadConfigurationFromFile(
					configurationFile, defaultConfiguration);
		} else {
			// No JVM property; Trying to find configuration file in classpath
			configurationFile = Keys.CUSTOM_PROPERTIES_LOCATION_CLASSPATH;
			loadConfiguration = ConfigurationFactory
					.loadConfigurationFromResource(configurationFile,
							defaultConfiguration);
		}
		// 1.JVM-params -> 2.properties file -> 3.default properties file
		return ConfigurationFactory.getSystemPropertiesStartingWith(
				Keys.PREFIX, loadConfiguration);
	}

	public static final Configuration createDefaultConfiguration() {
		return new Configuration(ConfigurationFactory.defaultConfiguration());
	}

	public static final Configuration createConfigurationFromFile(
			final String configurationFile) {
		return ConfigurationFactory.loadConfigurationFromFile(
				configurationFile, ConfigurationFactory.defaultConfiguration());
	}

	private static final Configuration defaultConfiguration() {
		return ConfigurationFactory.loadConfigurationFromResource(
				Keys.DEFAULT_PROPERTIES_LOCATION_CLASSPATH, null);
	}

	private static final Configuration loadConfigurationFromFile(
			final String propertiesFn, final Configuration defaultValues) {
		final Configuration properties = new Configuration(defaultValues);
		InputStream is = null; // NOPMD (null)
		try {
			try {
				is = new FileInputStream(propertiesFn);
			} catch (final FileNotFoundException ex) {
				// if not found as absolute path try within the classpath
				is = ConfigurationFactory.class.getClassLoader()
						.getResourceAsStream(propertiesFn);
				if (is == null) {
					return new Configuration(defaultValues);
				}
			}
			properties.load(is);
			return properties;
		} catch (final Exception ex) { // NOPMD NOCS (IllegalCatchCheck)
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (final IOException ex) {
				}
			}
		}
		return new Configuration(defaultValues);
	}

	private static final Configuration loadConfigurationFromResource(
			final String propertiesFn, final Configuration defaultValues) {
		final InputStream is = ConfigurationFactory.class.getClassLoader()
				.getResourceAsStream(propertiesFn);
		if (is != null) {
			try {
				final Configuration properties = new Configuration(
						defaultValues);
				properties.load(is);
				return properties;
			} catch (final Exception ex) { // NOPMD NOCS (IllegalCatchCheck)
			} finally {
				try {
					is.close();
				} catch (final IOException ex) {
				}
			}
		}
		return new Configuration(defaultValues);
	}

	private static final Configuration getSystemPropertiesStartingWith(
			final String prefix, final Configuration defaultValues) {
		final Configuration configuration = new Configuration(defaultValues);
		final Properties properties = System.getProperties();
		final Enumeration<?> keys = properties.propertyNames();
		while (keys.hasMoreElements()) {
			final String property = (String) keys.nextElement();
			if (property.startsWith(prefix)) {
				configuration.setProperty(property,
						properties.getProperty(property));
			}
		}
		return configuration;
	}
}
