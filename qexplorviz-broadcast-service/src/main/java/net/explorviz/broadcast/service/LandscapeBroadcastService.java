package net.explorviz.broadcast.service;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Fails due to an issue when injecting SSE with @Context issues:
 *  https://github.com/quarkusio/quarkus/issues/6515
 * Was resolved an should be included in Quarkus 1.5:
 *  https://github.com/quarkusio/quarkus/pull/9037
 */

/**
 * Broadcasts landscapes represented as strings to a set of registered sinks using SSE.
 */
//@Singleton
public class LandscapeBroadcastService implements SseBroadcast<String> {

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
   * Broadcast a JSON-API compliant stringified landscape to all registered clients.
   *
   * @param landscape - The to-be broadcasted landscape
   */
  @Override
  public void broadcast(String landscape) {
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
   * Registers a new sink to receive messages.
   * @param eventSink the sink to register
   */
  public void register(SseEventSink eventSink){
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
