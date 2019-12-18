package net.explorviz.settings.server.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.services.SettingsRepository;
import net.explorviz.settings.services.kafka.UserEventConsumer;
import net.explorviz.settings.services.kafka.UserEventHandler;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEvent.Type;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

/**
 * Primary starting class - executed, when the servlet context is started.
 */
@WebListener
public class SetupApplicationListener implements ApplicationEventListener {

  @Inject
  private SettingsRepository settingRepo;

  @Inject
  private UserEventConsumer userEventListener;

  @Inject
  private UserEventHandler userEventHandler;

  @Override
  public void onEvent(final ApplicationEvent event) {

    // After this type, CDI (e.g. injected LandscapeExchangeService) has been
    // fullfilled
    final Type t = Type.INITIALIZATION_FINISHED;


    if (event.getType().equals(t)) {
      this.addDefaultSettings();
      this.userEventListener.setHandler(this.userEventHandler);
      new Thread(this.userEventListener).start();
    }



  }

  @Override
  public RequestEventListener onRequest(final RequestEvent requestEvent) {
    return null;
  }


  /**
   * Adds the default settings to the database.
   */
  private void addDefaultSettings() {
    final String origin = "backend";

    // Workaround: Assign hardcoded ids
    final List<Setting> defaults = new ArrayList<>(Arrays.asList(
        new FlagSetting("showFpsCounter", "Show FPS Counter",
            "\'Frames Per Second\' metrics in visualizations", origin, false),
        new FlagSetting("appVizTransparency", "Enable Transparent Components",
            "Transparency effect for selection (left click) in application visualization", origin,
            true),
        new FlagSetting("keepHighlightingOnOpenOrClose", "Keep Highlighting On Open Or Close",
            "Toggle if highlighting should be resetted on double click in application"
                + " visualization",
            origin, true),
        new FlagSetting("enableHoverEffects", "Enable Hover Effects",
            "Hover effect (flashing entities) for mouse cursor", origin, true),
        new RangeSetting("appVizCommArrowSize", "Arrow Size in Application Visualization",
            "Arrow Size for selected communications in application visualization", origin, 1.0, 0.0,
            5.0),
        new RangeSetting("appVizTransparencyIntensity",
            "Transparency Intensity in Application Visualization",
            "Transparency effect intensity (\'Enable Transparent Components\' "
                + "must be enabled)",
            origin, 0.1, 0.1, 0.5),
        new RangeSetting("appVizCurvyCommHeight", "Curviness of the Communication Lines",
            "If greater 0.0, communication lines are rendered arc-shaped with set height"
                + " (Straight lines: 0.0)",
            origin, 0.0, 0.0, 50.0)));

    defaults.stream().forEach(this.settingRepo::createOrOverride);

  }

}
