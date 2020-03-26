package net.explorviz.analysis;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import net.explorviz.kiekeradapter.main.KiekerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  private static final int PROM_PORT = 1234;

  public static void main(final String[] args) {


    try {
      // Starts the server
      new HTTPServer(PROM_PORT);
      // JVM Metrics
      DefaultExports.initialize();
      LOGGER.info("Started prometheus server on port " + PROM_PORT);
    } catch (IOException e) {
      LOGGER.warn("Failed to start metrics HTTP server", e);
    }


    KiekerAdapter.getInstance().startReader();
  }

}
