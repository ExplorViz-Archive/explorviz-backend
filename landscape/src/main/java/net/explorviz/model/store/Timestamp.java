package net.explorviz.model.store;

import com.github.jasminb.jsonapi.annotations.Type;
import net.explorviz.model.helper.BaseEntity;

/**
 * Model representing timestamps (a single software landscape for a specific UNIX timestamp).
 */
@SuppressWarnings("serial")
@Type("timestamp")
public class Timestamp extends BaseEntity {

  private long timestampValue;
  private long calls;

  public Timestamp(final long timestampValue, final long calls) {
    super();
    this.setTimestampValue(timestampValue);
    this.setCalls(calls);
  }

  public Timestamp() {
    super();
    // explicit default constructor
  }

  public long getTimestampValue() {
    return this.timestampValue;
  }

  public void setTimestampValue(final long timestampValue) {
    this.timestampValue = timestampValue;
  }

  public long getCalls() {
    return this.calls;
  }

  public void setCalls(final long calls) {
    this.calls = calls;
  }

}
