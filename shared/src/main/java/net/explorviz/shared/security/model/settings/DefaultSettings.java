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

  private static void initDefaultValues() {
    booleanAttributes.put("showFpsCounter", false);
    booleanAttributes.put("appVizTransparency", true);
    booleanAttributes.put("enableHoverEffects", true);
    booleanAttributes.put("keepHighlightingOnOpenOrClose", true);

    numericAttributes.put("appVizCommArrowSize", 1.0);
    numericAttributes.put("appVizTransparencyIntensity", 0.3);
  }



  public static Map<String, Boolean> DEFAULT_BOOLEAN_SETTINGS() {
    return new HashMap<>(booleanAttributes);
  }

  public static Map<String, Number> DEFAULT_NUMERIC_SETTINGS() {
    return new HashMap<>(numericAttributes);
  }

  public static Map<String, String> DEFAULT_STRING_SETTINGS() {
    return new HashMap<>(stringAttributes);
  }


}
