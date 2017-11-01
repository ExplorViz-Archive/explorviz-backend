package net.explorviz.model;

import com.github.jasminb.jsonapi.annotations.Type
import net.explorviz.model.helper.BaseEntity
import org.eclipse.xtend.lib.annotations.Accessors
import com.github.jasminb.jsonapi.annotations.Relationship

@Type("timestamp")
class Timestamp extends BaseEntity {

	@Relationship("parent")
	@Accessors TimestampStorage parent

	@Accessors long calls

	new(long calls) {
		this.calls = calls
	}
}
