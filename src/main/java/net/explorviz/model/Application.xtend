package net.explorviz.model;

import java.util.ArrayList
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.helper.ELanguage
import net.explorviz.model.helper.DrawNodeEntity
import net.explorviz.model.helper.CommunicationAppAccumulator
import com.github.jasminb.jsonapi.annotations.Type
import com.github.jasminb.jsonapi.annotations.Relationship
import net.explorviz.model.helper.ColorDefinitions
import net.explorviz.math.Vector4f

@Type("application")
class Application extends DrawNodeEntity {
	//@Accessors var int id

	@Accessors var boolean database

	@Accessors var ELanguage programmingLanguage

	@Accessors long lastUsage

	@Relationship("parent")
	@Accessors Node parent

	@Relationship("components")
	@Accessors List<Component> components = new ArrayList<Component>


	@Accessors List<CommunicationClazz> communications = new ArrayList<CommunicationClazz>

	@Accessors val transient List<CommunicationAppAccumulator> communicationsAccumulated = new ArrayList<CommunicationAppAccumulator>

	@Relationship("incomingCommunications")
	@Accessors List<Communication> incomingCommunications = new ArrayList<Communication>
	
	@Accessors List<Communication> outgoingCommunications = new ArrayList<Communication>
	
	@Accessors List<DatabaseQuery> databaseQueries = new ArrayList<DatabaseQuery>
	
	@Accessors Vector4f backgroundColor = ColorDefinitions::applicationBackgroundColor
	
	new(String id) {
		this.id = id
	}


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
