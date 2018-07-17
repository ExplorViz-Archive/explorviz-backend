package net.explorviz.repository.discovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jasminb.jsonapi.ResourceConverter;

import net.explorviz.discovery.exceptions.agent.AgentNotFoundException;
import net.explorviz.discovery.exceptions.mapper.ResponseUtil;
import net.explorviz.discovery.model.Agent;
import net.explorviz.discovery.model.Procezz;
import net.explorviz.discovery.services.PropertyService;
import net.explorviz.server.injection.ResourceConverterFactory;
import net.explorviz.server.providers.JSONAPIProvider;

public class AgentRepository {

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(AgentRepository.class);

	private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

	private static final List<Agent> AGENTS = new ArrayList<Agent>();
	private static Map<Long, Procezz> ACTIVE_MONITORED_PROCEZZES = new HashMap<>();

	private static Logger LOGGER = LoggerFactory.getLogger(AgentRepository.class.getName());

	public String getUniqueIdString() {
		return String.valueOf(ID_GENERATOR.incrementAndGet());
	}

	public Agent lookupAgent(final Agent agent) {
		synchronized (AGENTS) {
			return AGENTS.stream().filter(Objects::nonNull).filter(a -> a.equals(agent)).findFirst().orElse(null);
		}
	}

	public static Agent lookupAgentStatic(final Agent agent) {
		synchronized (AGENTS) {
			return AGENTS.stream().filter(Objects::nonNull).filter(a -> a.equals(agent)).findFirst().orElse(null);
		}
	}

	public Agent registerAgent(final Agent agent) {
		synchronized (AGENTS) {
			final Agent possibleOldAgent = lookupAgent(agent);

			if (possibleOldAgent == null) {
				agent.setId(getUniqueIdString());
			} else {
				// re-registration
				// take old agent ID for new agent, since
				// otherwise Ember Store won't update the view for
				// an old remaining agent
				agent.setName(possibleOldAgent.getName());
				agent.setId(possibleOldAgent.getId());
				getAgents().remove(possibleOldAgent);
			}

			agent.setLastDiscoveryTime(System.currentTimeMillis());
			agent.setProcezzes(new ArrayList<Procezz>());
			getAgents().add(agent);
		}

		return agent;
	}

	public List<Procezz> insertIdsInProcezzList(final List<Procezz> procezzList) {

		for (final Procezz p : procezzList) {
			p.setId(getUniqueIdString());
		}

		return procezzList;

	}

	public Agent lookupAgentById(final String id) throws AgentNotFoundException {
		synchronized (AGENTS) {
			for (final Agent agent : AGENTS) {
				if (agent.getId().equals(id)) {
					return agent;
				}
			}
		}

		throw new AgentNotFoundException(ResponseUtil.ERROR_NO_AGENT_DETAIL, new Exception());

	}

	public List<Agent> getAgents() {
		return AGENTS;
	}

	public static Map<Long, Procezz> getActiveMonitoredProcezzesMap() {
		return ACTIVE_MONITORED_PROCEZZES;
	}

	public static List<Procezz> getActiveMonitoredProcezzes() {
		return new ArrayList<Procezz>(ACTIVE_MONITORED_PROCEZZES.values());
	}

	public static void filterActivelyMonitoredProcezzes() {
		synchronized (ACTIVE_MONITORED_PROCEZZES) {

			final long currentTimeWithOffset = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(30);

			// collect all procezzes, which are not older than 30 seconds or are
			// already sending trace information
			final Map<Long, Procezz> filtered = ACTIVE_MONITORED_PROCEZZES.entrySet().stream()
					.filter(x -> x.getKey() >= currentTimeWithOffset || x.getValue().wasFoundByBackend())
					.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

			// collect procezzes, which did not send any data since 30 seconds
			final Map<Long, Procezz> failedProcezzes = ACTIVE_MONITORED_PROCEZZES.entrySet().stream()
					.filter(x -> !filtered.containsKey(x.getKey()) && !filtered.containsValue(x.getValue()))
					.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

			for (final Procezz p : failedProcezzes.values()) {
				p.setErrorOccured(true);
				p.setErrorMessage("Monitoring failed. Backend did not receive any data for this procezz");

				final Agent agent = AgentRepository.lookupAgentStatic(p.getAgent());

				final String urlPath = PropertyService.getStringProperty("agentBaseURL")
						+ PropertyService.getStringProperty("agentProcezzPath");

				final String ipAndPort = agent.getIP() + ":" + agent.getPort();
				final String url = "http://" + ipAndPort + urlPath;

				doPatch(p, url);
			}

			// update all procezzes, which have been found a first time by the backend
			for (final Procezz p : filtered.values()) {

				if (p.isMonitoredFlag() && !p.wasFoundByBackend()) {
					p.setWasFoundByBackend(true);

					LOGGER.info("{}", "updating");

					final Agent agent = AgentRepository.lookupAgentStatic(p.getAgent());

					final String urlPath = PropertyService.getStringProperty("agentBaseURL")
							+ PropertyService.getStringProperty("agentProcezzPath");

					final String ipAndPort = agent.getIP() + ":" + agent.getPort();
					final String url = "http://" + ipAndPort + urlPath;

					doPatch(p, url);
				}
			}

			ACTIVE_MONITORED_PROCEZZES = filtered;

		}
	}

	public static <T> Response doPatch(final T t, final String url) throws ProcessingException {

		final ClientBuilder clientBuilder = ClientBuilder.newBuilder();

		final ResourceConverter converter = new ResourceConverterFactory().provide();

		clientBuilder.register(new JSONAPIProvider<>(converter));
		clientBuilder.register(new JSONAPIProvider<>(converter));

		return clientBuilder.build().target(url).request("application/vnd.api+json")
				.build("PATCH", Entity.entity(t, "application/vnd.api+json"))
				.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true).invoke();
	}

}