package net.explorviz.discovery.server.resources;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import net.explorviz.discovery.repository.discovery.AgentRepository;
import net.explorviz.shared.discovery.exceptions.agent.AgentNoConnectionException;
import net.explorviz.shared.discovery.exceptions.agent.AgentNotFoundException;
import net.explorviz.shared.discovery.exceptions.mapper.ResponseUtil;
import net.explorviz.shared.discovery.exceptions.procezz.ProcezzGenericException;
import net.explorviz.shared.discovery.model.Agent;
import net.explorviz.shared.discovery.model.Procezz;
import net.explorviz.shared.discovery.services.ClientService;
import net.explorviz.shared.discovery.services.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcezzResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcezzResource.class);

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final int UNPROCESSABLE_ENTITY = 422;

  private final AgentRepository agentRepository;
  private final ClientService clientService;

  @Inject
  public ProcezzResource(final ClientService clientService, final AgentRepository agentRepository) {
    this.agentRepository = agentRepository;
    this.clientService = clientService;
  }

  @PATCH
  @Path("{id}")
  @Consumes(MEDIA_TYPE)
  public Response updateProcess(@PathParam("id") final String procezzId, final Procezz procezz)
      throws ProcezzGenericException, AgentNotFoundException, AgentNoConnectionException {

    final String urlPath =
        PropertyService.getStringProperty("agentBaseURL") + "/" + procezz.getAgent().getId()
            + PropertyService.getStringProperty("agentProcezzPath") + "/" + procezzId;

    return this.forwardPatchRequest(procezz, urlPath);
  }

  @POST
  @Produces(MEDIA_TYPE)
  public List<Procezz> insertIdsInProcezzList(final List<Procezz> procezzList) {
    return this.agentRepository.insertIdsInProcezzList(procezzList);
  }

  private Response forwardPatchRequest(final Procezz procezz, final String urlPath)
      throws ProcezzGenericException, AgentNotFoundException, AgentNoConnectionException {

    final Optional<Agent> agentOptional =
        this.agentRepository.lookupAgentById(procezz.getAgent().getId());

    if (!agentOptional.isPresent()) {
      throw new WebApplicationException("No agent for this process is registered.",
          UNPROCESSABLE_ENTITY);
    }

    final String ipAndPort = agentOptional.get().getIP() + ":" + agentOptional.get().getPort();
    final String url = "http://" + ipAndPort + urlPath;

    LOGGER.info("Forwarding request to agent: {}", url);

    // See RFC5789 page 4 for appropriate status codes
    Response httpResponse;
    try {
      httpResponse = this.clientService.doPatch(procezz, url);
    } catch (final ProcessingException e) {
      throw new AgentNoConnectionException(ResponseUtil.ERROR_NO_AGENT_CONNECTION_DETAIL, e);
    }

    final int httpStatus = httpResponse.getStatus();

    if (httpStatus == Response.Status.OK.getStatusCode()) {
      final Procezz updatedProcezz = httpResponse.readEntity(Procezz.class);

      // return updated (possibly restarted) procezz to frontend
      return Response.status(httpStatus).entity(updatedProcezz).build();
    } else {
      final String error = httpResponse.readEntity(String.class);
      throw new ProcezzGenericException(error, new Exception());
    }
  }

}
