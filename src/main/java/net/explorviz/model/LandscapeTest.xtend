package net.explorviz.model;


import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.helper.BaseEntity
import com.github.jasminb.jsonapi.annotations.Type

@Type("landscape")
class LandscapeTest extends BaseEntity {
	@Accessors long hash
	@Accessors long activities
}
