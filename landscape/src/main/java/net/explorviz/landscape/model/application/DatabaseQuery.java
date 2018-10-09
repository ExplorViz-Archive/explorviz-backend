package net.explorviz.landscape.model.application;

import com.github.jasminb.jsonapi.annotations.Type;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing executed database queries.
 */
@SuppressWarnings("serial")
@Type("databasequery")
public class DatabaseQuery extends BaseEntity {

  private long timestamp;
  private String sqlStatement;
  private String returnValue;
  private long responseTime;

  public long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(final long timestamp) {
    this.timestamp = timestamp;
  }

  public String getSqlStatement() {
    return this.sqlStatement;
  }

  public void setSqlStatement(final String sqlStatement) {
    this.sqlStatement = sqlStatement;
  }

  public String getReturnValue() {
    return this.returnValue;
  }

  public void setReturnValue(final String returnValue) {
    this.returnValue = returnValue;
  }

  public long getResponseTime() {
    return this.responseTime;
  }

  public void setResponseTime(final long responseTime) {
    this.responseTime = responseTime;
  }

}
