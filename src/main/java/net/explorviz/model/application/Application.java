package net.explorviz.model.application;

import java.util.ArrayList;
import java.util.List;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.BaseEntity;
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

	@Relationship("databaseQueries")
	private List<DatabaseQuery> databaseQueries = new ArrayList<DatabaseQuery>();

	@Relationship("outgoingApplicationCommunications")
	private List<ApplicationCommunication> outgoingApplicationCommunications = new ArrayList<ApplicationCommunication>();

	@Relationship("outgoingClazzCommunications")
	private List<ClazzCommunication> outgoingClazzCommunications = new ArrayList<ClazzCommunication>();

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

	public void setDatabaseQueries(final List<DatabaseQuery> databaseQueries) {
		this.databaseQueries = databaseQueries;
	}

	public List<DatabaseQuery> getDatabaseQueries() {
		return databaseQueries;
	}

	public List<ApplicationCommunication> getOutgoingApplicationCommunications() {
		return outgoingApplicationCommunications;
	}

	public void setOutgoingApplicationCommunications(final List<ApplicationCommunication> outgoingCommunications) {
		this.outgoingApplicationCommunications = outgoingCommunications;
	}

	public List<ClazzCommunication> getOutgoingClazzCommunications() {
		return outgoingClazzCommunications;
	}

	public void setOutgoingClazzCommunications(final List<ClazzCommunication> outgoingClazzCommunications) {
		this.outgoingClazzCommunications = outgoingClazzCommunications;
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
		this.setOutgoingApplicationCommunications(new ArrayList<ApplicationCommunication>());
		this.setOutgoingClazzCommunications(new ArrayList<ClazzCommunication>());
	}

}
