package net.explorviz.settings.server.resources.test.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;


/**
 * Contains the default settings created at server startup
 */
public class DefaultSettings {


  public final static String origin = "backend";

  public static FlagSetting showFpsCounter = new FlagSetting("showFpsCounter", "Show FPS Counter",
      "\'Frames Per Second\' metrics in visualizations", origin, false);
  public static FlagSetting appVizTransparency = new FlagSetting("appVizTransparency",
      "Enable Transparent Components",
      "Transparency effect for selection (left click) in application visualization", origin, true);
  public static FlagSetting keepHighlightingOnOpenOrClose = new FlagSetting(
      "keepHighlightingOnOpenOrClose", "Keep Highlighting On Open Or Close",
      "Toggle if highlighting should be resetted on double click in application" + " visualization",
      origin, true);
  public static FlagSetting enableHoverEffects = new FlagSetting("enableHoverEffects",
      "Enable Hover Effects", "Hover effect (flashing entities) for mouse cursor", origin, true);

  public static RangeSetting appVizCommArrowSize = new RangeSetting("appVizCommArrowSize",
      "Arrow Size in Application Visualization",
      "Arrow Size for selected communications in application visualization", origin, 1.0, 0.0, 5.0);
  public static RangeSetting appVizTransparencyIntensity = new RangeSetting(
      "appVizTransparencyIntensity", "Transparency Intensity in Application Visualization",
      "Transparency effect intensity (\'Enable Transparent Components\' must be enabled)", origin,
      0.1, 0.1, 0.5);
  public static RangeSetting appVizCurvyCommHeight =
      new RangeSetting("appVizCurvyCommHeight", "Curviness of the Communication Lines",
          "If greater 0.0, communication lines are rendered arc-shaped with set height (Straight "
              + "lines: 0.0)",
          origin, 0.0, 0.0, 50.0);


  public static List<Setting> all = new ArrayList<>(Arrays.asList(showFpsCounter,
      appVizTransparency,
      keepHighlightingOnOpenOrClose,
      enableHoverEffects,
      appVizCommArrowSize,
      appVizTransparencyIntensity,
      appVizCurvyCommHeight));

}
