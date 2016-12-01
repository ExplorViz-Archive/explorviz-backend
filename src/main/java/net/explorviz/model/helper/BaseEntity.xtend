package net.explorviz.model.helper

import com.github.jasminb.jsonapi.annotations.Id
import java.io.Serializable
import org.eclipse.xtend.lib.annotations.Accessors

abstract class BaseEntity implements Serializable{
	
	@Id	
    @Accessors private String id;
}
