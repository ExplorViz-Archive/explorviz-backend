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

@Type("landscape")
class Landscape extends BaseEntity {
	@Accessors long hash
	@Accessors long activities

	@Relationship("systems")
	@Accessors List<System> systems = new ArrayList<System>

	@Relationship("applicationCommunication")
	@Accessors List<Communication> applicationCommunication = new ArrayList<Communication>

	@Accessors Map<Long, String> events = new TreeMap<Long, String>

	@Accessors Map<Long, String> errors = new TreeMap<Long, String>

	@Relationship("communicationsAccumulated")
	@Accessors val transient List<CommunicationAccumulator> communicationsAccumulated = new ArrayList<CommunicationAccumulator>(
		4)

	def void updateLandscapeAccess(long timeInNano) {
		setHash(timeInNano)
	}

	def void updateTimestamp(long timestamp) {
		setTimestamp(timestamp)
	}

	def void destroy() {
//		for (system : systems)
//			system.destroy()
//
//		for (applicationCommu : applicationCommunication)
//			applicationCommu.destroy()
	}
}
