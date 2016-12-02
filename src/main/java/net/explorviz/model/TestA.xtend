package net.explorviz.model

import com.github.jasminb.jsonapi.annotations.Type
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import java.util.List
import java.util.ArrayList
import org.eclipse.xtend.lib.annotations.Accessors

@Type("testa")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator, property = "id")
class TestA {
	
	@Id
	@Accessors private String id;

	@Accessors private String title;
	
	@Relationship("testbs")
	@Accessors private List<TestB> articles = new ArrayList;

	
}