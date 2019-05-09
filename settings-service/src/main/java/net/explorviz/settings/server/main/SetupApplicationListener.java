package net.explorviz.settings.server.main;

import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.services.MongoRepository;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEvent.Type;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.morphia.Datastore;

/**
 * Primary starting class - executed, when the servlet context is started.
 */
@WebListener
public class SetupApplicationListener implements ApplicationEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetupApplicationListener.class);

  private static final String ADMIN_NAME = "admin";

  @Inject
  private Datastore datastore;

  @Inject
  private MongoRepository<Setting, String> settingRepo;

  @Override
  public void onEvent(final ApplicationEvent event) {

    // After this type, CDI (e.g. injected LandscapeExchangeService) has been
    // fullfilled
    final Type t = Type.INITIALIZATION_FINISHED;


    if (event.getType().equals(t)) {
      this.addDefaultSettings();
    }

  }

  @Override
  public RequestEventListener onRequest(final RequestEvent requestEvent) {
    return null;
  }



  private void addDefaultSettings() {

    /*
     * final List<Setting<?>> defaults = new ArrayList<Setting<?>>(Arrays.asList( new
     * BooleanSetting("showFpsCounter", "Show FPS Counter",
     * "\'Frames Per Second\' metrics in visualizations", false, "backend"), new
     * BooleanSetting("appVizTransparency", "App Viz Transparency",
     * "Transparency effect for selection (left click) in application visualization", true,
     * "backend"), new BooleanSetting("enableHoverEffects", "Enable Hover Effects",
     * "Hover effect (flashing entities) for mouse cursor", true, "backend"), new
     * BooleanSetting("keepHighlightingOnOpenOrClose", "Keep Highlighting On Open Or Close",
     * "Toggle if highlighting should be resetted on double click in application visualization",
     * true, "backend"), new DoubleSetting("appVizCommArrowSize",
     * "Arrow Size in Application Visualization",
     * "Arrow Size for selected communications in application visualization", 1.0, "backend"), new
     * DoubleSetting("appVizTransparencyIntensity",
     * "Transparency Intensity in Application Visualization",
     * "Transparency effect intensity (\'App Viz Transparency\' must be enabled)", 0.1, "backend",
     * 0.5, 0.1)));
     *
     * defaults.stream().filter(d -> !this.settingRepo.find(d.getId()).isPresent())
     * .forEach(this.settingRepo::create);
     */

  }

}
