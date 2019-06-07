package net.explorviz.security.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.List;
import net.explorviz.shared.security.model.roles.Role;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Class that represents a batch request.
 */
@Type("userbatchrequest")
public class UserBatchRequest {

  /*
   * There is actually no need for an id since such object are only transmitted from the client to
   * the server. JSON-API enforces the existence of an @Id field.
   */
  @Id
  private final String id = "const";

  private final String prefix;
  private final int count;
  private final List<Role> roles;
  private final List<String> passwords;

  /**
   * Creates a new batch request.
   * 
   * @param prefix the prefix of names of all users that will be created with this requests.
   * @param count the amount of users to created
   * @param passwords a password for each user.
   * @param roles the roles each created user will have.
   */
  @JsonCreator
  public UserBatchRequest(@JsonProperty("prefix") final String prefix,
      @JsonProperty("count") final int count,
      @JsonProperty("password") final List<String> passwords,
      @JsonProperty("roles") final List<Role> roles) {
    this.prefix = prefix;
    this.count = count;
    this.roles = roles;
    this.passwords = passwords;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public int getCount() {
    return this.count;
  }

  public List<Role> getRoles() {
    return this.roles;
  }

  public List<String> getPasswords() {
    return this.passwords;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("Prefix", this.prefix)
        .append("Count", this.count)
        .append("Roles", this.roles)
        .toString();
  }



}
