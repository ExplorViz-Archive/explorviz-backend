package net.explorviz.model.store;

import com.github.jasminb.jsonapi.annotations.Type;
import net.explorviz.model.helper.BaseEntity;

/**
 * Model representing timestamps (a single software landscape for a specific UNIX timestamp).
 */
@SuppressWarnings("serial")
@Type("timestamp")
public class Timestamp extends BaseEntity {

  private long timestamp;
  private long calls;

  public Timestamp(final long timestamp, final long calls) {
    this.setTimestamp(timestamp);
    this.setCalls(calls);
  }

  public Timestamp() {}

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(final long timestamp) {
    this.timestamp = timestamp;
  }

  public long getCalls() {
    return calls;
  }

  public void setCalls(final long calls) {
    this.calls = calls;
  }

}
