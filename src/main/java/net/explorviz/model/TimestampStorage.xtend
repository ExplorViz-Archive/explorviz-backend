package net.explorviz.model

import net.explorviz.model.helper.BaseEntity
import com.github.jasminb.jsonapi.annotations.Type
import java.util.ArrayList
import com.github.jasminb.jsonapi.annotations.Relationship
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors

@Type("timestampstorage")
class TimestampStorage extends BaseEntity {

	@Relationship("timestamps")
	@Accessors List<Timestamp> timestamps = new ArrayList<Timestamp>
	
	new(String id) {
		this.id = id
	}
	
	def void addTimestamp(Timestamp t) {
		timestamps.add(t);
	}
}