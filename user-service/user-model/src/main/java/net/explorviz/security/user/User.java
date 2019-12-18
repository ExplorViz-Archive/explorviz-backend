package net.explorviz.security.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.IndexOptions;
import xyz.morphia.annotations.Indexed;

/**
 * Model class (container) for the pair of username and password.
 */
@Type("user")
@Entity("users")
public class User {

  @Id
  @com.github.jasminb.jsonapi.annotations.Id
  private String id;

  @Indexed(options = @IndexOptions(unique = true))
  private String username;

  @JsonProperty(access = Access.WRITE_ONLY)
  private String password;

  private List<String> roles = new ArrayList<>();

  private String batchId;
  
  
  private String token;

  public User() {
    // For MongoDB
  }

  public User(final String username) {
    this.username = username;
  }

  /**
   * Creates a new {@link User} object.
   *
   * @param id id of the user. Should be managed by the persistence layer
   * @param username the username
   * @param password the password
   * @param roles the roles
   */
  public User(final String id, final String username, final String password,
      final List<String> roles, final String batchId) {
    this.username = username;
    this.id = id;
    this.password = password;
    this.roles = roles;
    this.batchId = batchId;
  }
  
  public User(final String id, final String username, final String password,
      final List<String> roles) {
    this(id, username, password, roles, "");
  }


  public String getId() {
    return this.id;
  }


  public void setId(final String id) {
    this.id = id;
  }

  public String getUsername() {
    return this.username;
  }
  
  public void setUsername(final String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public List<String> getRoles() {
    return this.roles;
  }

  public void setRoles(final List<String> roles) {
    this.roles = roles;
  }


  public void setToken(final String newToken) {
    this.token = newToken;
  }

  public String getToken() {
    return this.token;
  }

  public String getBatchId() {
    return batchId;
  }
  
  public void setBatchId(String batchId) {
    this.batchId = batchId;
  }

}
