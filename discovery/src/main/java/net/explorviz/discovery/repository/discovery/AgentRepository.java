package net.explorviz.repository.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import net.explorviz.discovery.exceptions.agent.AgentNotFoundException;
import net.explorviz.discovery.exceptions.mapper.ResponseUtil;
import net.explorviz.discovery.model.Agent;
import net.explorviz.discovery.model.Procezz;

public class AgentRepository {

  // private static final Logger LOGGER =
  // LoggerFactory.getLogger(AgentRepository.class);

  private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

  private static final List<Agent> AGENTS = new ArrayList<>();

  public String getUniqueIdString() {
    return String.valueOf(ID_GENERATOR.incrementAndGet());
  }

  public List<Agent> getAgents() {
    return AGENTS;
  }

  public Agent lookupAgent(final Agent agent) {
    synchronized (AGENTS) {
      return AGENTS.stream().filter(Objects::nonNull).filter(a -> a.equals(agent)).findFirst()
          .orElse(null);
    }
  }

  public Agent registerAgent(final Agent agent) {
    synchronized (AGENTS) {
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

  public List<Procezz> insertIdsInProcezzList(final List<Procezz> procezzList) {

    for (final Procezz p : procezzList) {
      p.setId(this.getUniqueIdString());
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

}
