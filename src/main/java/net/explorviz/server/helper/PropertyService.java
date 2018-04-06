package net.explorviz.server.helper;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyService.class);

	private static final Properties PROP = new Properties();

	private PropertyService() {
		// don't instantiate
	}

	static {
		try {
			PROP.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("explorviz.properties"));
		} catch (final IOException e) {
			LOGGER.error("Couldn't load properties file. Is WEB-INF/explorviz.properties a valid file?. Exception: {}",
					e.getMessage());
		}
	}

	public static int getIntegerProperty(final String propName) {
		return Integer.valueOf(getStringProperty(propName));
	}

	public static String getStringProperty(final String propName) {
		return (String) PROP.get(propName);
	}

}
