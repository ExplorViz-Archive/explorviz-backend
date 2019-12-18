package net.explorviz.security.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.explorviz.security.user.User;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Class that represents a batch request.
 */
@Type("userbatchrequest")
public class UserBatchRequest {


  @Id
  private String id;

  private final String prefix;
  private final int count;
  private final List<String> roles;
  private final List<String> passwords;

  private final Map<String, Object> preferences;

  @Relationship("users")
  private List<User> users = new ArrayList<>();

  /**
   * Creates a new batch request.
   *
   * @param prefix the prefix of names of all users that will be created with this requests.
   * @param count the amount of users to created
   * @param passwords a password for each user.
   * @param roles the roles each created user will have.
   * @param preferences preferences for each user
   */
  @JsonCreator
  public UserBatchRequest(@JsonProperty("prefix") final String prefix,
      @JsonProperty("count") final int count,
      @JsonProperty("password") final List<String> passwords,
      @JsonProperty("roles") final List<String> roles,
      @JsonProperty("preferences") final Map<String, Object> preferences) {
    this.prefix = prefix;
    this.count = count;
    this.roles = roles;
    this.passwords = passwords;
    this.preferences = preferences;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public int getCount() {
    return this.count;
  }

  public List<String> getRoles() {
    return this.roles;
  }

  public List<String> getPasswords() {
    return this.passwords;
  }

  @Hidden
  public void setUsers(final List<User> users) {
    this.users = users;
  }

  // Why is this not in the API definition?
  @ArraySchema(arraySchema = @Schema(accessMode = AccessMode.READ_ONLY))
  public List<User> getUsers() {
    return this.users;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("Prefix", this.prefix)
        .append("Count", this.count)
        .append("Roles", this.roles)
        .toString();
  }


  public Map<String, Object> getPreferences() {
    return this.preferences;
  }

  public void setId(final String batchId) {
    this.id = batchId;

  }


}
