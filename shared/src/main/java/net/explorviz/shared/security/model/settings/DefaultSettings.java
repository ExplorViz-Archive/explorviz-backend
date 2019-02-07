package net.explorviz.shared.security.model.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultSettings {

  private static final Map<String, BooleanSettingDescriptor> booleanSettings = new HashMap<>();

  private static final Map<String, NumericSettingDescriptor> numericSettings = new HashMap<>();

  private static final Map<String, StringSettingDescriptor> stringSettings = new HashMap<>();


  static {
    initDefaultValues();
  }

  /**
   * Initializes the default settings.
   */
  private static void initDefaultValues() {
    booleanSettings.put("showFpsCounter",
        new BooleanSettingDescriptor("Show FPS Counter", "", false));
    booleanSettings.put("appVizTransparency",
        new BooleanSettingDescriptor("App Viz Transparency", "", true));
    booleanSettings.put("enableHoverEffects",
        new BooleanSettingDescriptor("Enable Hover Effects", "", true));
    booleanSettings.put("keepHighlightingOnOpenOrClose",
        new BooleanSettingDescriptor("Keep Highlighting On Open Or Close", "", true));

    numericSettings.put("appVizCommArrowSize",
        new NumericSettingDescriptor("AppViz Arrow Size", "", 1.0));
    numericSettings.put("appVizTransparencyIntensity", new NumericSettingDescriptor(
        "AppViz Transparency Intensity", "Intesity of the transparency effect", 0.5, 0.1, 0.1));

  }


  /**
   * Creates a new map containing the default boolean settings
   *
   * @return the default boolean settings.
   */
  public static Map<String, Boolean> DEFAULT_BOOLEAN_SETTINGS() {

    return booleanSettings.entrySet().stream()
        .collect(Collectors.toMap(v -> v.getKey(), v -> v.getValue().getDefaultValue()));
  }

  /**
   * Creates a new map containing the default numeric settings
   *
   * @return the default numeric settings.
   */
  public static Map<String, Number> DEFAULT_NUMERIC_SETTINGS() {

    return numericSettings.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getDefaultValue()));

  }

  /**
   * Creates a new map containing the default text based settings
   *
   * @return the default string settings.
   */
  public static Map<String, String> DEFAULT_STRING_SETTINGS() {
    return stringSettings.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getDefaultValue()));
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

    final Map<String, Boolean> defaultBools = DEFAULT_BOOLEAN_SETTINGS();
    final Map<String, String> defaultStrings = DEFAULT_STRING_SETTINGS();
    final Map<String, Number> defaultNum = DEFAULT_NUMERIC_SETTINGS();

    for (final String key : defaultBools.keySet()) {
      if (settings.getBooleanAttribute(key) == null) {
        settings.put(key, defaultBools.get(key));
        changed = true;
      }
    }

    for (final String key : defaultNum.keySet()) {
      if (settings.getNumericAttribute(key) == null) {
        settings.put(key, defaultNum.get(key));
        changed = true;
      }
    }

    for (final String key : defaultStrings.keySet()) {
      if (settings.getNumericAttribute(key) == null) {
        settings.put(key, defaultStrings.get(key));
        changed = true;
      }
    }


    return changed;
  }

}
