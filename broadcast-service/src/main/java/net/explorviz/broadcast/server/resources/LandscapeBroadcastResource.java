package net.explorviz.broadcast.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSink;
import net.explorviz.broadcast.server.helper.LandscapeBroadcastService;
import net.explorviz.shared.security.filters.Secure;

/**
 * Resource class that contains an endpoint which clients, e.g., the ExplorViz Frontend, can use to
 * register to landscape updates.
 *
 */
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "token", scheme = "bearer",
    bearerFormat = "JWT")
@SecurityRequirement(name = "token")
@Tag(name = "Landscape Broadcast")
@Path("v1/landscapes/broadcast")
@Secure
@PermitAll
public class LandscapeBroadcastResource {

  private final LandscapeBroadcastService broadcastService;

  @Inject
  public LandscapeBroadcastResource(final LandscapeBroadcastService broadcastService) {
    this.broadcastService = broadcastService;
  }

  /**
   * Endpoint that clients can use to register for landscape updates.
   *
   * @param eventSink - The to-be registered event sink.
   * @param response - {@link HttpServletResponse} which is enriched with header information.
   */
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Operation(description = "Endpoint that clients can use to register for landscape updates.",
      summary = "Register for Landscape updates")
  public void listenToBroadcast(@Context final SseEventSink eventSink,
      @Context final HttpServletResponse response) {

    // https://serverfault.com/a/801629
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("X-Accel-Buffering", "no");

    this.broadcastService.register(eventSink);
  }
}
