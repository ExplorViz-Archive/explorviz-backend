package net.explorviz.landscape.model.store;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.annotations.Type;
import net.explorviz.landscape.model.helper.BaseEntity;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Model representing timestamps (a single software landscape for a specific UNIX timestamp in
 * milliseconds).
 */
@SuppressWarnings("serial")
@Type("timestamp")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "super.id")
public class Timestamp extends BaseEntity {

  private long unixTimestamp;
  private int totalRequests;

  /**
   * Constructs a new timestamp.
   *
   * @param id             the id
   * @param timestampValue the actual unix epoch timestamp
   * @param requests       the total amount of requests at this point in time
   */
  @JsonCreator
  public Timestamp(@JsonProperty("id") final String id,
                   @JsonProperty("timestampValue") final long timestampValue,
                   @JsonProperty("requests") final int requests) {
    super(id);
    this.setUnixTimestamp(timestampValue);
    this.setTotalRequests(requests);
  }

  public long getUnixTimestamp() {
    return this.unixTimestamp;
  }

  public void setUnixTimestamp(final long unixTimestamp) {
    this.unixTimestamp = unixTimestamp;
  }

  public int getTotalRequests() {
    return this.totalRequests;
  }

  public void setTotalRequests(final int totalRequests) {
    this.totalRequests = totalRequests;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(this.unixTimestamp).append(this.totalRequests)
        .append(this.id)
        .build();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final Timestamp other = (Timestamp) obj;
    if (this.unixTimestamp != other.unixTimestamp) {
      return false;
    }
    return this.totalRequests == other.totalRequests;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append(this.id).append(this.unixTimestamp).toString();
  }


}
