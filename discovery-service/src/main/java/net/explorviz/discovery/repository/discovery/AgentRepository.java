package net.explorviz.discovery.repository.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Inject;
import net.explorviz.discovery.server.services.BroadcastService;
import net.explorviz.shared.discovery.model.Agent;
import net.explorviz.shared.discovery.model.Procezz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In-Memory repository for {@link Agent}s.
 */
public class AgentRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(AgentRepository.class);

  private final AtomicLong idGenerator = new AtomicLong(0);

  private final List<Agent> agents = new ArrayList<>();

  private final BroadcastService broadcastService;

  /**
   * Creates a new repository. Handled by DI.
   * @param broadcastService the used
   */
  @Inject
  public AgentRepository(final BroadcastService broadcastService) {
    this.broadcastService = broadcastService;
  }

  private String getUniqueIdString() {
    return String.valueOf(this.idGenerator.incrementAndGet());
  }

  /**
   * Get all agents.
   *
   * @return list of all registered agents.
   */
  public List<Agent> getAgents() {
    return this.agents;
  }

  /**
   * Find a specific agent.
   *
   * @param agent the agent to search for.
   * @return the agent if it exists in this repository or {@code null} otherwise
   */
  public Agent lookupAgent(final Agent agent) {
    synchronized (this.agents) {
      return this.agents.stream()
          .filter(Objects::nonNull)
          .filter(a -> a.equals(agent))
          .findFirst()
          .orElse(null);
    }
  }

  /**
   * Register a new agent. If the agent to register already existed, it is re-registered with the
   * same Id.
   *
   * @param agent the agent to register
   * @return the registered agent
   */
  public Agent registerAgent(final Agent agent) {
    synchronized (this.agents) {
      final Agent possibleOldAgent = this.lookupAgent(agent);

      if (possibleOldAgent == null) {
        agent.setId(this.getUniqueIdString());
      } else {
        // re-registration
        // take old agent ID for new agent, since
        // otherwise Ember Store won't update the view for
        // an old remaining agent
        agent.setName(possibleOldAgent.getName());
        agent.setId(possibleOldAgent.getId());
        this.getAgents().remove(possibleOldAgent);
      }

      agent.setLastDiscoveryTime(System.currentTimeMillis());
      agent.setProcezzes(new ArrayList<Procezz>());
      this.getAgents().add(agent);
    }

    return agent;
  }

  /**
   * Generates and assigns a unique Id for each process in a list.
   *
   * @param procezzList the list of {@link Procezz} objects
   * @return the same list of procezzes where each object has an Id now
   */
  public List<Procezz> insertIdsInProcezzList(final List<Procezz> procezzList) {

    for (final Procezz p : procezzList) {
      p.setId(this.getUniqueIdString());
    }

    return procezzList;

  }

  /**
   * Find an agent by its Id.
   *
   * @param id the Id to look for
   * @return An optional containing the agent with the given Id if found
   */
  public Optional<Agent> lookupAgentById(final String id) {
    synchronized (this.agents) {
      for (final Agent agent : this.agents) {
        if (agent.getId().equals(id)) {
          return Optional.of(agent);
        }
      }
    }

    return Optional.empty();
  }

  /**
   * Updates an existing agent. Fails silently if such agent does not exist.
   * @param a the agent to update
   */
  public void updateAgent(final Agent a) {
    synchronized (this.agents) {
      final Agent potentialAgent = this.lookupAgent(a);

      if (potentialAgent != null) {
        LOGGER.info("updating internal agent with name: {}", potentialAgent.getIPPortOrName());

        this.agents.remove(potentialAgent);
        this.agents.add(a);

        this.broadcastService.broadcastMessage(this.agents);
      }
    }
  }


  public void exchangeProcezzInAgent(final Procezz proc, final Agent a) {

    synchronized (this.agents) {

      final Agent potentialAgent = this.lookupAgent(a);

      Optional<Integer> potentialProcezzIndex = Optional.empty();

      for (int i = 0; i < potentialAgent.getProcezzes().size(); i++) {
        final Procezz p = potentialAgent.getProcezzes().get(i);

        if (p.getId().equals(proc.getId())) {
          potentialProcezzIndex = Optional.of(i);
        }
      }

      if (potentialProcezzIndex.isPresent()) {

        final String nameOrPid =
            proc.getName() == null ? proc.getName() : String.valueOf(proc.getPid());

        LOGGER.info("Updating internal procezz with id {} of agent with pid/name {}",
            nameOrPid,
            potentialAgent.getIPPortOrName());

        potentialAgent.getProcezzes().remove(potentialProcezzIndex.get().intValue());
        potentialAgent.getProcezzes().add(proc);
        proc.setAgent(potentialAgent);
      }

    }
  }

}
