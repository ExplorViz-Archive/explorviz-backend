package net.explorviz.broadcast.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

/**
 * Uses reactive streams to retrieve the most recent landscape updates
 * from a kafka topic.
 */
@ApplicationScoped
public class LandscapeUpdateListener {

  private static final String LANDSCAPE_STREAM = "landscape-update";

  // Sink for retrieved landscapes
  private SseBroadcast<String> sink;

  @Inject
  public LandscapeUpdateListener(SseBroadcast<String> sink) {
    this.sink = sink;
  }

  /**
   * Retrieves landscape updates from a kafka topic as an reactive stream.
   * @param update the most recently generated landscape
   */
  @Incoming(LANDSCAPE_STREAM)
  void retrieveLandscape(String update) {
    sink.broadcast(update);
  }
}
