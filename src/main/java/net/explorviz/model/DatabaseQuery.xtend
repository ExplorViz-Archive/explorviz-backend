package net.explorviz.model;

import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.helper.BaseEntity
import com.github.jasminb.jsonapi.annotations.Type

@Type("databasequery")
class DatabaseQuery extends BaseEntity{
	@Accessors String SQLStatement
	@Accessors String returnValue
	
	@Accessors long timeInNanos
	
	new(String id) {
		this.id = id
	}
}
