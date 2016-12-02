package net.explorviz.model

import com.github.jasminb.jsonapi.annotations.Type
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import org.eclipse.xtend.lib.annotations.Accessors
import com.github.jasminb.jsonapi.RelType

@Type("testa")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator, property = "id")
class TestB {
	
	@Id
	@Accessors private String id;

	@Accessors private String title;
	
	@Relationship(value = "parent", resolve = true, relType = RelType.RELATED)
	@Accessors private TestA parent;

	
}