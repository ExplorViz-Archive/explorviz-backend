package net.explorviz.common.live_trace_processing.configuration;

import java.util.Enumeration;
import java.util.Properties;

public final class Configuration extends Properties {

	private static final long serialVersionUID = 3364877592243422259L;

	public Configuration() {
		this(null);
	}

	public Configuration(final Properties defaults) {
		super(defaults);
	}

	public final String getStringProperty(final String key) {
		final String s = super.getProperty(key);
		return s == null ? "" : s.trim();
	}

	public final boolean getBooleanProperty(final String key) {
		return Boolean.parseBoolean(getStringProperty(key));
	}

	public final int getIntProperty(final String key, final int defaultValue) {
		final String s = getStringProperty(key);
		try {
			return Integer.parseInt(s);
		} catch (final NumberFormatException ex) {
			return defaultValue;
		}
	}

	public final long getLongProperty(final String key) {
		final String s = getStringProperty(key);
		try {
			return Long.parseLong(s);
		} catch (final NumberFormatException ex) {
			return 0;
		}
	}

	public final String[] getStringArrayProperty(final String key) {
		return this.getStringArrayProperty(key, "\\|");
	}

	public final void setStringArrayProperty(final String key,
			final String[] value) {
		setProperty(key, Configuration.toProperty(value));
	}

	public final String[] getStringArrayProperty(final String key,
			final String split) {
		final String s = getStringProperty(key);
		if (s.length() == 0) {
			return new String[0];
		} else {
			return s.split(split);
		}
	}

	public static final String toProperty(final Object[] values) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			sb.append(values[i]);
			if (i < values.length - 1) {
				sb.append('|');
			}
		}
		return sb.toString();
	}

	public final Configuration getPropertiesStartingWith(final String prefix) {
		final Configuration configuration = new Configuration(null);
		final Enumeration<?> keys = propertyNames();
		while (keys.hasMoreElements()) {
			final String property = (String) keys.nextElement();
			if (property.startsWith(prefix)) {
				configuration
						.setProperty(property, super.getProperty(property));
			}
		}
		return configuration;
	}
}
