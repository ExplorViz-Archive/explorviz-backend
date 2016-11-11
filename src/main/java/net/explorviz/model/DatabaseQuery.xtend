package net.explorviz.model;

import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.helper.BaseEntity

class DatabaseQuery extends BaseEntity{
	@Accessors String SQLStatement
	@Accessors String returnValue
	
	@Accessors long timeInNanos
}
