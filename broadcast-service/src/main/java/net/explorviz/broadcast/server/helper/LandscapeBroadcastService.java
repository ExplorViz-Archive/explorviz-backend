package net.explorviz.broadcast.server.helper;

import javax.inject.Singleton;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import net.explorviz.landscape.model.landscape.Landscape;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link LandscapeBroadcastService} sends Server-Side events containing landscape objects to
 * registered clients.
 *
 */
@Service
@Singleton
public class LandscapeBroadcastService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeBroadcastService.class);
  private static final MediaType APPLICATION_JSON_API_TYPE =
      new MediaType("application", "vnd.api+json");

  private static final String SSE_EVENT_NAME = "message";

  private final Sse sse;
  private final SseBroadcaster broadcaster;

  /**
   * Creates a new broadcast service.
   *
   * @param sse - Sse entry point
   */
  public LandscapeBroadcastService(@Context final Sse sse) {
    this.sse = sse;
    this.broadcaster = sse.newBroadcaster();

    this.broadcaster.onClose(this::onCloseOperation);
    this.broadcaster.onError(this::onErrorOperation);
  }

  /**
   * Broadcast a landscape to all registered clients.
   *
   * @param landscape - The to-be broadcasted landscape
   */
  public void broadcastMessage(final Landscape landscape) {
    final OutboundSseEvent event = this.sse.newEventBuilder()
        .name(SSE_EVENT_NAME)
        .mediaType(APPLICATION_JSON_API_TYPE)
        .data(landscape)
        .build();

    this.broadcaster.broadcast(event);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Broadcast new landscape to clients."); // NOCS
    }
  }

  /**
   * Broadcast a JSON-API compliant stringified landscape to all registered clients.
   *
   * @param jsonApiLandscape - The to-be broadcasted landscape
   */
  public void broadcastMessage(final String jsonApiLandscape) {
    final OutboundSseEvent event = this.sse.newEventBuilder()
        .name(SSE_EVENT_NAME)
        .mediaType(APPLICATION_JSON_API_TYPE)
        .data(jsonApiLandscape)
        .build();

    this.broadcaster.broadcast(event);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Broadcast new landscape to clients."); // NOCS
    }
  }

  public void register(final SseEventSink eventSink) {
    this.broadcaster.register(eventSink);
  }

  private void onCloseOperation(final SseEventSink sink) { // NOPMD
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("SseEventSink closed");
    }
  }

  private void onErrorOperation(final SseEventSink sink, final Throwable e) { // NOPMD
    if (LOGGER.isErrorEnabled()) {
      LOGGER.error("Broadcasting to a SseEventSink failed. "
          + "This may not be a problem, since there is no way to unregister.", e);
    }
  }

}
