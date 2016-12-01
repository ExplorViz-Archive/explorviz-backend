package net.explorviz.model;

import java.util.ArrayList
import java.util.List
import java.util.Map
import java.util.TreeMap
import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.helper.CommunicationAccumulator
import net.explorviz.model.helper.BaseEntity
import com.github.jasminb.jsonapi.annotations.Type
import com.github.jasminb.jsonapi.annotations.Relationship
import com.fasterxml.jackson.annotation.JsonIgnore

@Type("landscape")
class Landscape extends BaseEntity {
	@Accessors long hash
	@Accessors long activities

	@JsonIgnore
	@Accessors List<System> systems = new ArrayList<System>

	@JsonIgnore
	@Accessors List<Communication> applicationCommunication = new ArrayList<Communication>

	@JsonIgnore
	@Accessors Map<Long, String> events = new TreeMap<Long, String>

	@JsonIgnore
	@Accessors Map<Long, String> errors = new TreeMap<Long, String>

	@JsonIgnore
	@Accessors val transient List<CommunicationAccumulator> communicationsAccumulated = new ArrayList<CommunicationAccumulator>(
		4)

	def void updateLandscapeAccess(long timeInNano) {
		setHash(timeInNano)
	}

	def void destroy() {
//		for (system : systems)
//			system.destroy()
//
//		for (applicationCommu : applicationCommunication)
//			applicationCommu.destroy()
	}
}
