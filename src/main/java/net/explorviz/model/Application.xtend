package net.explorviz.model;

import java.util.ArrayList
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.helper.ELanguage
import net.explorviz.model.helper.DrawNodeEntity
import net.explorviz.model.helper.CommunicationAppAccumulator
import com.github.jasminb.jsonapi.annotations.Type
import com.github.jasminb.jsonapi.annotations.Relationship

@Type("application")
class Application extends DrawNodeEntity {
	//@Accessors var int id

	@Accessors var boolean database

	@Accessors var ELanguage programmingLanguage

	@Accessors long lastUsage

	@Relationship("parent")
	@Accessors Node parent

	@Relationship("components")
	@Accessors var List<Component> components = new ArrayList<Component>

	@Accessors var List<CommunicationClazz> communications = new ArrayList<CommunicationClazz>

	@Accessors val transient List<CommunicationAppAccumulator> communicationsAccumulated = new ArrayList<CommunicationAppAccumulator>

	@Accessors var List<Communication> incomingCommunications = new ArrayList<Communication>
	@Accessors var List<Communication> outgoingCommunications = new ArrayList<Communication>
	
	@Accessors var List<DatabaseQuery> databaseQueries = new ArrayList<DatabaseQuery>


	def void clearAllPrimitiveObjects() {
		for (component : components)
			component.clearAllPrimitiveObjects()

	//		communicationsAccumulated.forEach[it.clearAllPrimitiveObjects()] done in extra method
	}

	def void unhighlight() {
		for (component : components)
			component.unhighlight()
	}

	def void openAllComponents() {
		for (component : components)
			component.openAllComponents()
	}
	
	def void closeAllComponents() {
		for (component : components)
			component.closeAllComponents()
	}

}
