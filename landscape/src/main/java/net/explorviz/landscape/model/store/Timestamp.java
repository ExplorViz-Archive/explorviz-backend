package net.explorviz.landscape.model.store;

import com.github.jasminb.jsonapi.annotations.Type;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing timestamps (a single software landscape for a specific UNIX timestamp).
 */
@SuppressWarnings("serial")
@Type("timestamp")
public class Timestamp extends BaseEntity {

  private long timestamp;
  private int totalRequests;

  public Timestamp(final long timestampValue, final int requests) {
    super();
    this.setTimestamp(timestampValue);
    this.setTotalRequests(requests);
  }

  public Timestamp() {
    super();
    // explicit default constructor
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(final long timestamp) {
    this.timestamp = timestamp;
  }

  public int getTotalRequests() {
    return this.totalRequests;
  }

  public void setTotalRequests(final int totalRequests) {
    this.totalRequests = totalRequests;
  }

}
