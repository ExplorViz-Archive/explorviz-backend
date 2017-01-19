package net.explorviz.model;

import com.github.jasminb.jsonapi.annotations.Type
import net.explorviz.model.helper.BaseEntity
import org.eclipse.xtend.lib.annotations.Accessors
import com.github.jasminb.jsonapi.annotations.Relationship

@Type("timestamp")
class Timestamp extends BaseEntity {
	
	@Relationship("parent")
	@Accessors TimestampStorage parent
	
	@Accessors long timestamp
	@Accessors long calls
		
	new(Integer id, long timestamp, long calls) {
		this.id = Integer.toString(id)
		this.timestamp = timestamp
		this.calls = calls
	}
}
