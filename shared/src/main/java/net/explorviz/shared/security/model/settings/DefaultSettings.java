package net.explorviz.shared.security.model.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
        new BooleanSettingDescriptor("showFpsCounter", "Show FPS Counter", "", false));
    booleanSettings.put("appVizTransparency",
        new BooleanSettingDescriptor("appVizTransparency", "App Viz Transparency", "", true));
    booleanSettings.put("enableHoverEffects",
        new BooleanSettingDescriptor("enableHoverEffects", "Enable Hover Effects", "", true));
    booleanSettings.put("keepHighlightingOnOpenOrClose", new BooleanSettingDescriptor(
        "keepHighlightingOnOpenOrClose", "Keep Highlighting On Open Or Close", "", true));

    numericSettings.put("appVizCommArrowSize",
        new NumericSettingDescriptor("appVizCommArrowSize", "AppViz Arrow Size", "", 1.0));
    numericSettings.put("appVizTransparencyIntensity",
        new NumericSettingDescriptor("appVizTransparencyIntensity", "AppViz Transparency Intensity",
            "Intesity of the transparency effect", 0.1, 0.5, 0.1));

  }

  /**
   *
   * @return The descriptors for boolean settings.
   */
  public static Map<String, BooleanSettingDescriptor> booleanSettings() {
    return booleanSettings;
  }

  /**
   *
   * @return The descriptors for string based settings.
   */
  public static Map<String, StringSettingDescriptor> stringSettings() {
    return stringSettings;
  }

  /**
   *
   * @return The descriptors for numeric settings.
   */
  public static Map<String, NumericSettingDescriptor> numericSettings() {
    return numericSettings;
  }

  /**
   * Creates a new map containing the default boolean settings
   *
   * @return the default boolean settings.
   */
  public static Map<String, Boolean> booleanDefaults() {

    return booleanSettings.entrySet().stream()
        .collect(Collectors.toMap(v -> v.getKey(), v -> v.getValue().getDefaultValue()));
  }

  /**
   * Creates a new map containing the default numeric settings
   *
   * @return the default numeric settings.
   */
  public static Map<String, Double> numericDefaults() {

    return numericSettings.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getDefaultValue()));

  }

  /**
   * Creates a new map containing the default text based settings
   *
   * @return the default string settings.
   */
  public static Map<String, String> stringDefaults() {
    return stringSettings.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getDefaultValue()));
  }


  /**
   * Adds default values to all missing settings in a given {@link UserSettings} object. Removes all
   * settings, that have no counterpart in the default settings.
   *
   *
   * @param settings the settings to process.
   * @return {@code true} if and only if the given settings were changed.
   */
  public static void makeConform(final UserSettings settings) {

    final Map<String, Boolean> defaultBools = booleanDefaults();
    final Map<String, String> defaultStrings = stringDefaults();
    final Map<String, Double> defaultNum = numericDefaults();

    for (final String key : defaultBools.keySet()) {
      if (settings.getBooleanAttributes().get(key) == null) {
        settings.put(key, defaultBools.get(key));
      }
    }

    for (final String key : defaultNum.keySet()) {
      if (settings.getNumericAttributes().get(key) == null) {
        settings.put(key, defaultNum.get(key));
      }
    }

    for (final String key : defaultStrings.keySet()) {
      if (settings.getNumericAttributes().get(key) == null) {
        settings.put(key, defaultStrings.get(key));
      }
    }

    // Remove all keys that are not in defaults
    final Set<String> unknownBoolKeys = settings.getBooleanAttributes().keySet().stream()
        .filter(e -> !booleanSettings.containsKey(e)).collect(Collectors.toSet());

    unknownBoolKeys.forEach(k -> settings.getBooleanAttributes().remove(k));

    final Set<String> unkownNumKeys = settings.getNumericAttributes().keySet().stream()
        .filter(e -> !numericSettings.containsKey(e)).collect(Collectors.toSet());

    unkownNumKeys.forEach(k -> settings.getNumericAttributes().remove(k));

    final Set<String> unkownStringkeys = settings.getStringAttributes().keySet().stream()
        .filter(e -> !stringSettings.containsKey(e)).collect(Collectors.toSet());


    unkownStringkeys.forEach(k -> settings.getStringAttributes().remove(k));


  }

}
