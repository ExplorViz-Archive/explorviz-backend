package net.explorviz.server.security

import org.eclipse.xtend.lib.annotations.Accessors
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import net.explorviz.model.helper.BaseEntity
import com.github.jasminb.jsonapi.annotations.Type

@Entity(name = "USERS")
@Table(name = "USERS")
@Type("user")
class User extends BaseEntity{

	
	@Id 
	@Accessors String username
	@Accessors String password
	@Accessors String hashedPassword
	@Accessors String token
	@Accessors boolean isAuthenticated
	
	new (Long id) {
		this.id = id
	}
	
	new (String username, String hashedPassword) {
		this.username = username
		this.hashedPassword = hashedPassword
	}
	
	// Default constructor for Hibernate
	new() {}
	
}