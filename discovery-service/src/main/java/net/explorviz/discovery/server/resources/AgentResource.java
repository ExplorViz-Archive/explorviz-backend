package net.explorviz.discovery.server.resources;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.Sse;
import net.explorviz.discovery.repository.discovery.AgentRepository;
import net.explorviz.shared.common.provider.JsonApiProvider;
import net.explorviz.shared.config.helper.PropertyHelper;
import net.explorviz.shared.discovery.exceptions.agent.AgentInternalErrorException;
import net.explorviz.shared.discovery.exceptions.agent.AgentNoConnectionException;
import net.explorviz.shared.discovery.exceptions.agent.AgentNotFoundException;
import net.explorviz.shared.discovery.exceptions.mapper.ResponseUtil;
import net.explorviz.shared.discovery.model.Agent;
import net.explorviz.shared.discovery.model.Procezz;
import net.explorviz.shared.discovery.services.ClientService;
import net.explorviz.shared.security.filters.Secure;
import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SecurityScheme(type = SecuritySchemeType.HTTP, name = "token", scheme = "bearer",
    bearerFormat = "JWT")
@Path("v1/agents")
public class AgentResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(AgentResource.class);

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final MediaType JSON_API_TYPE = new MediaType("application", "vnd.api+json");
  private static final int UNPROCESSABLE_ENTITY = 422;

  private final AgentRepository agentRepository;
  private final ResourceConverter converter;
  private final ClientService clientService;

  @Inject
  public AgentResource(final ResourceConverter converter, final AgentRepository agentRepository,
      final ClientService clientService) {
    this.agentRepository = agentRepository;
    this.converter = converter;
    this.clientService = clientService;

    this.clientService.registerProviderReader(new JsonApiProvider<>(converter));
    this.clientService.registerProviderWriter(new JsonApiProvider<>(converter));
  }

  @Tag(name = "Agent Broadcast")
  @Operation(description = "Endpoint that clients can use to register for agent updates.",
      summary = "Register for agent updates")
  @ApiResponse(description = "Updated agent objects with procezzes of the discovery agents.",
      content = @Content(mediaType = MediaType.SERVER_SENT_EVENTS,
          schema = @Schema(implementation = Agent.class)))
  @SecurityRequirement(name = "token")
  @Secure
  @PermitAll
  @Path("/broadcast")
  public AgentBroadcastSubResource getAgentBroadcastResource(@Context final Sse sse,
      @Context final AgentBroadcastSubResource agentBroadcastSubResource) {
    return agentBroadcastSubResource;
  }

  @Operation(description = "Endpoint that clients can use to obtain all procezzes for an agent id.",
      summary = "Get procezzes for agent id.")
  @ApiResponse(description = "List of procezzes for the passed agent id.",
      content = @Content(mediaType = MediaType.SERVER_SENT_EVENTS,
          schema = @Schema(implementation = Procezz.class)))
  @SecurityRequirement(name = "token")
  @Secure
  @PermitAll
  @Path("{id}/procezzes")
  public ProcezzResource getProcezzResource(
      @Parameter(description = "Id of the agent.") @PathParam("id") final String agentID)
      throws AgentNotFoundException {

    final Optional<Agent> agentOptional = this.agentRepository.lookupAgentById(agentID);

    if (agentOptional.isPresent()) {
      return new ProcezzResource(this.clientService, this.agentRepository);
    } else {
      throw new WebApplicationException("No agent for this process is registered.",
          UNPROCESSABLE_ENTITY);
    }

  }

  @Operation(
      description = "Endpoint that discovery agents can use to register, so that the frontend will get their data.",
      summary = "Register discovery agent.")
  @ApiResponse(description = "List of procezzes for the passed agent id.",
      content = @Content(mediaType = MediaType.SERVER_SENT_EVENTS,
          schema = @Schema(implementation = Procezz.class)))

  @RequestBody(description = "TODO",
      content = @Content(schema = @Schema(implementation = Agent.class)))
  @POST
  @Consumes(MEDIA_TYPE)
  public Agent registerAgent(final Agent newAgent) throws DocumentSerializationException {

    // Attention, registration of MessageBodyReader implementation (JsonApiProvier) is mandatory
    final Client client = ClientBuilder.newBuilder()
        .register(SseFeature.class)
        .register(new JsonApiProvider<>(this.converter))
        .build();
    final WebTarget target =
        client.target("http://" + newAgent.getIP() + ":" + newAgent.getPort() + "/broadcast/");
    final EventSource eventSource = EventSource.target(target).build();

    final EventListener listener = new EventListener() {
      @Override
      public void onEvent(final InboundEvent inboundEvent) {

        LOGGER.info("Received SSE");
        // Type notation is mandatory
        final Agent a = inboundEvent.readData(Agent.class, JSON_API_TYPE);
        AgentResource.this.agentRepository.updateAgent(a);
      }
    };
    eventSource.register(listener, "message");
    eventSource.open();
    // eventSource.close();
    LOGGER.info("Agent registered");

    return this.agentRepository.registerAgent(newAgent);
  }

  @Operation(summary = "Update an agent")
  @ApiResponse(responseCode = "422", description = "No agent with the given id exists.")
  @ApiResponse(responseCode = "200",
      description = "Update successful, response contains the updated agent.",
      content = @Content(schema = @Schema(implementation = Agent.class)))
  @RequestBody(description = "TODO",
      content = @Content(schema = @Schema(implementation = Agent.class)))
  @PATCH
  @Path("{id}")
  @Consumes(MEDIA_TYPE)
  public Agent patchAgent(
      @Parameter(description = "Id of th agent.") @PathParam("id") final String agentID,
      final Agent agent) throws AgentInternalErrorException, AgentNoConnectionException {

    final Optional<Agent> agentOptional = this.agentRepository.lookupAgentById(agentID);

    if (!agentOptional.isPresent()) {
      throw new WebApplicationException("No agent for this process is registered.",
          UNPROCESSABLE_ENTITY);
    }

    final String urlPath = PropertyHelper.getStringProperty("agentBaseURL")
        + PropertyHelper.getStringProperty("agentAgentPath");

    final String ipAndPort = agent.getIP() + ":" + agent.getPort();
    final String url = "http://" + ipAndPort + urlPath;

    // See RFC5789 page 4 for appropriate status codes

    return this.clientService.doAgentPatchRequest(agent, url);
  }

  @Operation(summary = "Update an agent")
  @ApiResponse(responseCode = "422", description = "No agent with the given id exists.")
  @ApiResponse(responseCode = "200",
      description = "Update successful, response contains the updated agent.",
      content = @Content(schema = @Schema(implementation = Agent.class)))
  @RequestBody(description = "TODO",
      content = @Content(schema = @Schema(implementation = Agent.class)))
  @SecurityRequirement(name = "token")
  @GET
  @Produces(MEDIA_TYPE)
  public Response forwardAgentListRequest() throws DocumentSerializationException {

    final List<Agent> listToBeReturned = new ArrayList<>();

    final List<Agent> agentList = this.agentRepository.getAgents();

    final String urlPath = PropertyHelper.getStringProperty("agentBaseURL")
        + PropertyHelper.getStringProperty("agentAgentPath");

    for (final Agent agent : agentList) {

      if (agent.getId() == null) {
        continue;
      }

      final String ipAndPort = agent.getIP() + ":" + agent.getPort();
      final String url = "http://" + ipAndPort + urlPath;

      try {
        final Agent agentObject = this.clientService.doGETRequest(Agent.class, url, null);

        if (agentObject != null) {
          listToBeReturned.add(agentObject);
        }

      } catch (final ProcessingException e) {
        agent.setErrorOccured(true);
        agent.setErrorMessage(ResponseUtil.ERROR_NO_AGENT_CONNECTION_DETAIL);
        listToBeReturned.add(agent);
      }
    }

    return Response
        .ok(this.converter.writeDocumentCollection(new JSONAPIDocument<>(listToBeReturned)))
        .type(MEDIA_TYPE)
        .build();

  }

}
