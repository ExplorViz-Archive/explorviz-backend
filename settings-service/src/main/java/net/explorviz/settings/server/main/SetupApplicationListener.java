package net.explorviz.settings.server.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import net.explorviz.settings.services.MongoRepository;
import net.explorviz.settings.model.BooleanSetting;
import net.explorviz.settings.model.DoubleSetting;
import net.explorviz.settings.model.Setting;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.server.monitoring.ApplicationEvent.Type;
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
      addDefaultSettings();
    }

  }

  @Override
  public RequestEventListener onRequest(final RequestEvent requestEvent) {
    return null;
  }



  private void addDefaultSettings() {
    List<Setting<?>> defaults = new ArrayList<Setting<?>>(Arrays.asList(
        new BooleanSetting("showFpsCounter", "Show FPS Counter", "\'Frames Per Second\' metrics in visualizations", false),
        new BooleanSetting("appVizTransparency", "App Viz Transparency", "Transparency effect for selection (left click) in application visualization", true),
        new BooleanSetting("enableHoverEffects", "Enable Hover Effects", "Hover effect (flashing entities) for mouse cursor", true),
        new BooleanSetting("keepHighlightingOnOpenOrClose", "Keep Highlighting On Open Or Close", "Toggle if highlighting should be resetted on double click in application visualization", true),
        new DoubleSetting("appVizCommArrowSize", "Arrow Size in Application Visualization", "Arrow Size for selected communications in application visualization", 1.0),
        new DoubleSetting("appVizTransparencyIntensity", "Transparency Intensity in Application Visualization", "Transparency effect intensity (\'App Viz Transparency\' must be enabled)", 0.1, 0.5, 0.1)
      ));
    
    defaults.stream()  
      .filter(d -> !settingRepo.find(d.getId()).isPresent()).forEach(settingRepo::create);
    
  }

}