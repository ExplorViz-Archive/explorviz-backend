package net.explorviz.model;

import java.util.ArrayList;
import java.util.List;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.communication.ApplicationCommunication;
import net.explorviz.model.communication.ClazzCommunication;
import net.explorviz.model.helper.DrawNodeEntity;
import net.explorviz.model.helper.ELanguage;

@SuppressWarnings("serial")
@Type("application")
public class Application extends DrawNodeEntity {

	private boolean database;
	private ELanguage programmingLanguage;
	private long lastUsage;

	@Relationship("parent")
	private Node parent;

	@Relationship("components")
	private final List<Component> components = new ArrayList<Component>();

	@Relationship("outgoingApplicationCommunications")
	private List<ApplicationCommunication> outgoingApplicationCommunications = new ArrayList<ApplicationCommunication>();

	@Relationship("databaseQueries")
	private List<DatabaseQuery> databaseQueries = new ArrayList<DatabaseQuery>();

	public boolean isDatabase() {
		return database;
	}

	public void setDatabase(final boolean database) {
		this.database = database;
	}

	public ELanguage getProgrammingLanguage() {
		return programmingLanguage;
	}

	public void setProgrammingLanguage(final ELanguage programmingLanguage) {
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

	public void openAllComponents() {
		for (final Component component : this.getComponents()) {
			component.openAllComponents();
		}
	}

	public void closeAllComponents() {
		for (final Component component : this.getComponents()) {
			component.closeAllComponents();
		}
	}

	/**
	 * Get all outgoing clazz communications for the specific application
	 */
	public List<ClazzCommunication> getOutgoingClazzCommunication() {

		final List<ClazzCommunication> outgoingClazzCommunicationList = new ArrayList<ClazzCommunication>();

		for (final Component component : this.getComponents()) {
			for (final Clazz clazz : component.getClazzes()) {
				for (final ClazzCommunication clazzCommunication : clazz.getOutgoingCommunications()) {
					outgoingClazzCommunicationList.add(clazzCommunication);
				}
			}
		}

		return outgoingClazzCommunicationList;
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