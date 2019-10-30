package net.explorviz.discovery.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSink;
import net.explorviz.discovery.repository.discovery.AgentRepository;
import net.explorviz.discovery.server.services.BroadcastService;
import net.explorviz.shared.security.filters.Secure;

@Singleton
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

  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Operation(summary = "TODO")
  public void listenToBroadcast(@Context final SseEventSink eventSink,
      @Context final HttpServletResponse response) {

    // https://serverfault.com/a/801629
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("X-Accel-Buffering", "no");

    this.broadcastService.register(eventSink, this.agentRepository.getAgents());
  }
}
