package net.explorviz.server.security

import org.eclipse.xtend.lib.annotations.Accessors
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id

@Entity(name = "USERS")
@Table(name = "USERS")
class User {

	
	@Id 
	@Accessors String username
	@Accessors String hashedPassword
	@Accessors String token
	
	new (String username, String hashedPassword) {
		this.username = username
		this.hashedPassword = hashedPassword
	}
	
	// Default constructor for Hibernate
	new() {}
	
}