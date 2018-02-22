package net.explorviz.model.application;

import java.util.ArrayList;
import java.util.List;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.BaseEntity;
import net.explorviz.model.helper.ClazzCommunicationHelper;
import net.explorviz.model.helper.EProgrammingLanguage;
import net.explorviz.model.landscape.Node;

/**
 * Model representing a single application with a software landscape
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
@SuppressWarnings("serial")
@Type("application")
public class Application extends BaseEntity {

	private String name;
	private EProgrammingLanguage programmingLanguage;
	private long lastUsage;

	@Relationship("parent")
	private Node parent;

	@Relationship("components")
	private final List<Component> components = new ArrayList<Component>();

	@Relationship("outgoingApplicationCommunications")
	private List<ApplicationCommunication> outgoingApplicationCommunications = new ArrayList<ApplicationCommunication>();

	@Relationship("databaseQueries")
	private List<DatabaseQuery> databaseQueries = new ArrayList<DatabaseQuery>();

	@Relationship("outgoingClazzCommunications")
	// workaround until frontend is able to generate this list for rendering
	private List<ClazzCommunication> outgoingClazzCommunications = new ArrayList<ClazzCommunication>();

	@Relationship("aggregatedOutgoingClazzCommunications")
	private List<ClazzCommunication> aggregatedOutgoingClazzCommunications = new ArrayList<ClazzCommunication>();

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public EProgrammingLanguage getProgrammingLanguage() {
		return programmingLanguage;
	}

	public void setProgrammingLanguage(final EProgrammingLanguage programmingLanguage) {
		this.programmingLanguage = programmingLanguage;
	}

	public long getLastUsage() {
		return lastUsage;
	}

	public void setLastUsage(final long lastUsage) {
		this.lastUsage = lastUsage;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(final Node parent) {
		this.parent = parent;
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setOutgoingApplicationCommunication(final List<ApplicationCommunication> outgoingCommunications) {
		this.outgoingApplicationCommunications = outgoingCommunications;
	}

	public List<ApplicationCommunication> getOutgoingApplicationCommunications() {
		return outgoingApplicationCommunications;
	}

	public void setDatabaseQueries(final List<DatabaseQuery> databaseQueries) {
		this.databaseQueries = databaseQueries;
	}

	public List<DatabaseQuery> getDatabaseQueries() {
		return databaseQueries;
	}

	public List<ClazzCommunication> getOutgoingClazzCommunications() {
		return outgoingClazzCommunications;
	}

	public void setOutgoingClazzCommunications(final List<ClazzCommunication> outgoingClazzCommunications) {
		this.outgoingClazzCommunications = outgoingClazzCommunications;
	}

	public List<ClazzCommunication> getAggregatedClazzCommunications() {
		return aggregatedOutgoingClazzCommunications;
	}

	public void setAggregatedOutgoingClazzCommunications(
			final List<ClazzCommunication> aggregatedOutgoingClazzCommunications) {
		this.aggregatedOutgoingClazzCommunications = aggregatedOutgoingClazzCommunications;
	}

	/**
	 * Get all outgoing clazz communications for the specific application
	 */
	private List<ClazzCommunication> computeOutgoingClazzCommunications() {

		final List<ClazzCommunication> outgoingClazzCommunicationFinalList = new ArrayList<ClazzCommunication>();

		for (final Component component : this.getComponents()) {

			// add all found items (for a single component) to the final list
			outgoingClazzCommunicationFinalList
					.addAll(ClazzCommunicationHelper.getChildrenComponentClazzCommunications(component));
		}

		return outgoingClazzCommunicationFinalList;
	}

	/**
	 * Aggregates outgoing clazzCommunications with the same sourceClazz and
	 * targetClazz and updates the attributes
	 */
	public void calculateAggregatedOutgoingClazzCommunications() {

		// compute all outgoing clazz communications
		this.setOutgoingClazzCommunications(this.computeOutgoingClazzCommunications());
		final List<ClazzCommunication> singleOutgoingClazzCommunications = this.computeOutgoingClazzCommunications();

		// aggregate similar clazz communications
		final List<ClazzCommunication> aggregatedOutgoingClazzCommunications = new ArrayList<ClazzCommunication>();

		for (final ClazzCommunication singleClazzCommunication : singleOutgoingClazzCommunications) {
			// if not exists, create an aggregated clazzcommunication
			if (aggregatedOutgoingClazzCommunications.isEmpty()) {
				final ClazzCommunication newAggregatedClazzCommunication = new ClazzCommunication();
				newAggregatedClazzCommunication.setOperationName(singleClazzCommunication.getOperationName());
				newAggregatedClazzCommunication.setRequestsCacheCount(singleClazzCommunication.getRequestsCacheCount());
				newAggregatedClazzCommunication.setSourceClazz(singleClazzCommunication.getSourceClazz());
				newAggregatedClazzCommunication.setTargetClazz(singleClazzCommunication.getTargetClazz());

				aggregatedOutgoingClazzCommunications.add(newAggregatedClazzCommunication);
			}

			for (final ClazzCommunication aggregatedClazzCommunication : aggregatedOutgoingClazzCommunications) {

				if (aggregatedClazzCommunication.getId() != singleClazzCommunication.getId()
						&& (singleClazzCommunication.getSourceClazz() == aggregatedClazzCommunication.getSourceClazz())
						&& (singleClazzCommunication.getTargetClazz() == aggregatedClazzCommunication
								.getTargetClazz())) {

					aggregatedClazzCommunication
							.setRequestsCacheCount(aggregatedClazzCommunication.getRequestsCacheCount()
									+ singleClazzCommunication.getRequestsCacheCount());
					aggregatedOutgoingClazzCommunications.add(singleClazzCommunication);
				}
			}
		}

		this.setAggregatedOutgoingClazzCommunications(aggregatedOutgoingClazzCommunications);
	}

	/**
	 * Clears all existings communication from (landscape-perspective) and within
	 * the application (application-perspective)
	 */
	public void clearCommunication() {
		for (final Component component : this.getComponents()) {
			for (final Clazz clazz : component.getClazzes()) {
				clazz.clearCommunication();
			}
		}
		this.setOutgoingApplicationCommunication(new ArrayList<ApplicationCommunication>());
	}

}