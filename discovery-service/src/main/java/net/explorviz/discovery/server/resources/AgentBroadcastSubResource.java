package net.explorviz.discovery.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSink;
import net.explorviz.discovery.repository.discovery.AgentRepository;
import net.explorviz.discovery.server.services.BroadcastService;
import net.explorviz.shared.security.filters.Secure;

/**
 * Resource class that contains an endpoint which clients, e.g., the ExplorViz Frontend, can use to
 * register to agent updates.
 *
 */
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "token", scheme = "bearer",
    bearerFormat = "JWT")
@SecurityRequirement(name = "token")
@Secure
@PermitAll
public class AgentBroadcastSubResource {

  // private static final Logger LOGGER = LoggerFactory.getLogger(AgentBroadcastSubResource.class);

  private final BroadcastService broadcastService;
  private final AgentRepository agentRepository;

  @Inject
  public AgentBroadcastSubResource(final BroadcastService broadcastService,
      final AgentRepository agentRepository) {
    this.broadcastService = broadcastService;
    this.agentRepository = agentRepository;
  }

  // curl -v -X GET http://localhost:8081/v1/landscapes/broadcast/ -H
  // "Content-Type:
  // text/event-stream"

  /**
   * Endpoint that clients can use to register for agent updates.
   *
   * @param eventSink - The to-be registered event sink.
   * @param response - {@link HttpServletResponse} which is enriched with header information.
   */
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Operation(description = "Endpoint that clients can use to register for agent updates.",
      summary = "Register for agent updates")
  @ApiResponse(description = "If registered, returns updated agent objects with "
      + "procezzes of the discovery agents.")
  public void listenToBroadcast(@Context final SseEventSink eventSink,
      @Context final HttpServletResponse response) {

    // https://serverfault.com/a/801629
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("X-Accel-Buffering", "no");

    this.broadcastService.register(eventSink, this.agentRepository.getAgents());
  }
}
