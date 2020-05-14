package net.explorviz.broadcast.resources;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSink;
import net.explorviz.broadcast.service.SseBroadcast;

/**
 * Resource clients can use to register for periodic landscape updates,
 * broadcasted via SSE.
 */
@Path("v1/landscapes/broadcast")
public class LandscapeBroadcastResource {

  private final SseBroadcast<String> broadcastService;

  @Inject
  public LandscapeBroadcastResource(final SseBroadcast<String> service) {
    this.broadcastService = service;
  }

  /**
   * Endpoint that clients can use to register for landscape updates.
   *
   * @param eventSink - The to-be registered event sink.
   * @param response  - {@link HttpServletResponse} which is enriched with header information.
   */
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public void listenToBroadcast(@Context final SseEventSink eventSink,
                                @Context final HttpServletResponse response) {
    if (eventSink == null) {
      throw new IllegalStateException("No client connected.");
    }
    // https://serverfault.com/a/801629
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("X-Accel-Buffering", "no");

    this.broadcastService.register(eventSink);
  }


}
