package net.explorviz.shared.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.shared.security.model.roles.Role;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.IndexOptions;
import xyz.morphia.annotations.Indexed;
import xyz.morphia.annotations.Reference;

/**
 * Model class (container) for the pair of username and password.
 */
@Type("user")
@Entity("users")
public class User {

  @Id
  @com.github.jasminb.jsonapi.annotations.Id(LongIdHandler.class)
  private Long id;

  @Indexed(options = @IndexOptions(unique = true))
  private String username;

  @JsonProperty(access = Access.WRITE_ONLY)
  private String password;

  @Reference
  @Relationship("roles")
  private List<Role> roles = new ArrayList<>();

  // No relationship or reference due to embedded structure
  // Composition, not aggregation
  private UserSettings settings = new UserSettings();

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
  public User(final Long id, final String username, final String password, final List<Role> roles) {
    this.username = username;
    this.id = id;
    this.password = password;
    this.roles = roles;
  }


  public User(final Long id, final String username, final String password, final List<Role> roles,
      final UserSettings settings) { // NOPMD
    this(id, username, password, roles);
    this.settings = settings;
  }


  public Long getId() {
    return this.id;
  }


  public void setId(final Long id) {
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

  public List<Role> getRoles() {
    return this.roles;
  }

  public void setRoles(final List<Role> roles) {
    this.roles = roles;
  }

  public UserSettings getSettings() {
    return this.settings;
  }

  public void setSettings(final UserSettings settings) {
    this.settings = settings;
  }

  public void setToken(final String newToken) {
    this.token = newToken;
  }

  public String getToken() {
    return this.token;
  }



}
