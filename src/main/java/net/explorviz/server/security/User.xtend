package net.explorviz.server.security

import org.eclipse.xtend.lib.annotations.Accessors
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.GeneratedValue

@Entity
@Table(name = "USERS")
class User {
	
	@Id 
    @GeneratedValue
	@Accessors int id
	
	@Accessors String username
	@Accessors String hashedPassword
	@Accessors String salt
	
	new (int id, String username, String hashedPassword, String salt) {
		this.id = id
		this.username = username
		this.hashedPassword = hashedPassword
		this.salt = salt
	}
	
}