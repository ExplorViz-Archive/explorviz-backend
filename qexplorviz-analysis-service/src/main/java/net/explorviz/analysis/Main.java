package net.explorviz.analysis;

import explorviz.live_trace_processing.main.MonitoringStringRegistry;
import io.quarkus.runtime.Startup;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import net.explorviz.kiekeradapter.main.KiekerAdapter;

@Startup
@Singleton
public class Main {

  @PostConstruct
  public void run() {
    KiekerAdapter.getInstance().startReader();
  }

}
