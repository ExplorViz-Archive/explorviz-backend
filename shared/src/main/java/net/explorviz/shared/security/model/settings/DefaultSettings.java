package net.explorviz.shared.security.model.settings;

import java.util.HashMap;
import java.util.Map;

public class DefaultSettings {

  private static final Map<String, Boolean> booleanAttributes = new HashMap<>();

  private static final Map<String, Number> numericAttributes = new HashMap<>();;

  private static final Map<String, String> stringAttributes = new HashMap<>();;


  static {
    initDefaultValues();
  }

  /**
   * Initializes the default settings.
   */
  private static void initDefaultValues() {
    booleanAttributes.put("showFpsCounter", false);
    booleanAttributes.put("appVizTransparency", true);
    booleanAttributes.put("enableHoverEffects", true);
    booleanAttributes.put("keepHighlightingOnOpenOrClose", true);

    numericAttributes.put("appVizCommArrowSize", 1.0);
    numericAttributes.put("appVizTransparencyIntensity", 0.3);

  }


  /**
   * Creates a new map containing the default boolean settings
   *
   * @return the default boolean settings.
   */
  public static Map<String, Boolean> DEFAULT_BOOLEAN_SETTINGS() {
    return new HashMap<>(booleanAttributes);
  }

  /**
   * Creates a new map containing the default numeric settings
   *
   * @return the default numeric settings.
   */
  public static Map<String, Number> DEFAULT_NUMERIC_SETTINGS() {
    return new HashMap<>(numericAttributes);
  }

  /**
   * Creates a new map containing the default text based settings
   *
   * @return the default string settings.
   */
  public static Map<String, String> DEFAULT_STRING_SETTINGS() {
    return new HashMap<>(stringAttributes);
  }


  /**
   * Adds default values to all missing settings in a given {@link UserSettings} object.
   *
   * TODO: Remove unknown setting values.
   *
   * @param settings the settings to process.
   * @return {@code true} if and only if the given settings were changed.
   */
  public static boolean addMissingDefaults(final UserSettings settings) {
    boolean changed = false;

    for (final String key : booleanAttributes.keySet()) {
      if (settings.getBooleanAttribute(key) == null) {
        settings.put(key, booleanAttributes.get(key));
        changed = true;
      }
    }

    for (final String key : numericAttributes.keySet()) {
      if (settings.getNumericAttribute(key) == null) {
        settings.put(key, numericAttributes.get(key));
        changed = true;
      }
    }

    for (final String key : stringAttributes.keySet()) {
      if (settings.getNumericAttribute(key) == null) {
        settings.put(key, stringAttributes.get(key));
        changed = true;
      }
    }


    return changed;
  }

}
