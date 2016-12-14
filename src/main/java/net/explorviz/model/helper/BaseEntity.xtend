package net.explorviz.model.helper

import com.github.jasminb.jsonapi.annotations.Id
import java.io.Serializable
import org.eclipse.xtend.lib.annotations.Accessors
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator, property = "id")
abstract class BaseEntity implements Serializable{
	
	@Id
    @Accessors private String id;
}
