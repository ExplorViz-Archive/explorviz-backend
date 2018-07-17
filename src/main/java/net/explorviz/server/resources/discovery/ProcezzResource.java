package net.explorviz.server.resources.discovery;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import net.explorviz.discovery.exceptions.agent.AgentNoConnectionException;
import net.explorviz.discovery.exceptions.agent.AgentNotFoundException;
import net.explorviz.discovery.exceptions.mapper.ResponseUtil;
import net.explorviz.discovery.exceptions.procezz.ProcezzGenericException;
import net.explorviz.discovery.model.Agent;
import net.explorviz.discovery.model.Procezz;
import net.explorviz.discovery.services.ClientService;
import net.explorviz.discovery.services.PropertyService;
import net.explorviz.repository.discovery.AgentRepository;

@Path("discovery")
public class ProcezzResource {

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(ProcezzResource.class);

	private static final String MEDIA_TYPE = "application/vnd.api+json";

	private final AgentRepository agentRepository;
	private final ClientService clientService;

	@Inject
	public ProcezzResource(final ClientService clientService, final AgentRepository agentRepository) {
		this.agentRepository = agentRepository;
		this.clientService = clientService;
	}

	@PATCH
	@Path("procezz")
	@Consumes(MEDIA_TYPE)
	public Response updateProcess(final Procezz procezz)
			throws ProcezzGenericException, AgentNotFoundException, AgentNoConnectionException {

		final String urlPath = PropertyService.getStringProperty("agentBaseURL")
				+ PropertyService.getStringProperty("agentProcezzPath");

		return forwardPatchRequest(procezz, urlPath);
	}

	@POST
	@Path("procezzes")
	@Produces(MEDIA_TYPE)
	public List<Procezz> insertIdsInProcezzList(final List<Procezz> procezzList) {
		return this.agentRepository.insertIdsInProcezzList(procezzList);
	}

	private Response forwardPatchRequest(final Procezz procezz, final String urlPath)
			throws ProcezzGenericException, AgentNotFoundException, AgentNoConnectionException {

		final Agent agent = this.agentRepository.lookupAgentById(procezz.getAgent().getId());

		final String ipAndPort = agent.getIP() + ":" + agent.getPort();
		final String url = "http://" + ipAndPort + urlPath;

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

			// TODO
			if (updatedProcezz.isMonitoredFlag() && !updatedProcezz.wasFoundByBackend()) {
				AgentRepository.getActiveMonitoredProcezzesMap().put(System.currentTimeMillis(), updatedProcezz);
			}

			// return updated (possibly restarted) procezz to frontend
			return Response.status(httpStatus).entity(updatedProcezz).build();
		} else {
			final String error = httpResponse.readEntity(String.class);
			throw new ProcezzGenericException(error, new Exception());
		}
	}

}
