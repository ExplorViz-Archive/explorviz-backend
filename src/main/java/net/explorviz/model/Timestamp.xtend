package net.explorviz.model;

import com.github.jasminb.jsonapi.annotations.Type
import net.explorviz.model.helper.BaseEntity
import org.eclipse.xtend.lib.annotations.Accessors

@Type("timestamp")
class Timestamp extends BaseEntity {
	
	@Accessors long calls

	new(long timestamp, long calls) {
		this.timestamp = timestamp
		this.calls = calls
	}
}