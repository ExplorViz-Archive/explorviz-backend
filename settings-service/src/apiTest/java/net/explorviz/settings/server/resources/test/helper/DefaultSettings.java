package net.explorviz.settings.server.resources.test.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;

// CHECKSTYLE.OFF: MagicNumberCheck
// CHECKSTYLE.OFF: MultipleStringLiteralsCheck


/**
 * Contains the default settings created at server startup.
 */
public class DefaultSettings {


  public static final String ORIGIN = "backend";

  public static final FlagSetting
      SHOW_FPS_COUNTER = new FlagSetting("showFpsCounter", "Show FPS Counter",
      "\'Frames Per Second\' metrics in visualizations", ORIGIN, false);
  public static final FlagSetting APP_VIZ_TRANSPARENCY = new FlagSetting("appVizTransparency",
      "Enable Transparent Components",
      "Transparency effect for selection (left click) in application visualization", ORIGIN, true);
  public static final FlagSetting KEEP_HIGHLIGHTING_ON_OPEN_OR_CLOSE = new FlagSetting(
      "keepHighlightingOnOpenOrClose", "Keep Highlighting On Open Or Close",
      "Toggle if highlighting should be resetted on double click in application" + " visualization",
      ORIGIN, true);
  public static final FlagSetting ENABLE_HOVER_EFFECTS = new FlagSetting("enableHoverEffects",
      "Enable Hover Effects", "Hover effect (flashing entities) for mouse cursor", ORIGIN, true);

  public static final RangeSetting APP_VIZ_COMM_ARROW_SIZE = new RangeSetting("appVizCommArrowSize",
      "Arrow Size in Application Visualization",
      "Arrow Size for selected communications in application visualization", ORIGIN, 1.0, 0.0, 5.0);
  public static final RangeSetting APP_VIZ_TRANSPARENCY_INTENSITY = new RangeSetting(
      "appVizTransparencyIntensity", "Transparency Intensity in Application Visualization",
      "Transparency effect intensity (\'Enable Transparent Components\' must be enabled)", ORIGIN,
      0.1, 0.1, 0.5);
  public static final RangeSetting APP_VIZ_CURVY_COMM_HEIGHT =
      new RangeSetting("appVizCurvyCommHeight", "Curviness of the Communication Lines",
          "If greater 0.0, communication lines are rendered arc-shaped with set height (Straight "
              + "lines: 0.0)",
          ORIGIN, 0.0, 0.0, 50.0);


  public static final List<Setting> ALL = new ArrayList<>(Arrays.asList(SHOW_FPS_COUNTER,
      APP_VIZ_TRANSPARENCY,
      KEEP_HIGHLIGHTING_ON_OPEN_OR_CLOSE,
      ENABLE_HOVER_EFFECTS,
      APP_VIZ_COMM_ARROW_SIZE,
      APP_VIZ_TRANSPARENCY_INTENSITY,
      APP_VIZ_CURVY_COMM_HEIGHT));

}
