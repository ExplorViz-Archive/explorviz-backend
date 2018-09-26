package net.explorviz.server.helper;

import javax.inject.Singleton;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import net.explorviz.model.landscape.Landscape;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Singleton
public class BroadcastService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastService.class);
  private static final MediaType APPLICATION_JSON_API_TYPE =
      new MediaType("application", "vnd.api+json");

  private final Sse sse;
  private final SseBroadcaster broadcaster;

  public BroadcastService(@Context final Sse sse) {
    this.sse = sse;
    this.broadcaster = sse.newBroadcaster();

    this.broadcaster.onClose(this::onCloseOperation);
    this.broadcaster.onError(this::onErrorOperation);
  }

  public void broadcastMessage(final Landscape landscape) {
    final OutboundSseEvent event = this.sse.newEventBuilder().name("message")
        .mediaType(APPLICATION_JSON_API_TYPE).data(landscape).build();

    this.broadcaster.broadcast(event);
  }

  public void register(final SseEventSink eventSink) {
    this.broadcaster.register(eventSink);
  }

  private void onCloseOperation(final SseEventSink sink) { // NOPMD
    LOGGER.info("SseEventSink closed");
  }

  private void onErrorOperation(final SseEventSink sink, final Throwable e) { // NOPMD
    LOGGER.error(
        "Broadcasting to a SseEventSink failed. This may not be a problem, since there is no way to unregister.",
        e);
  }

}
