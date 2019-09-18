package net.explorviz.landscape.model.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import net.explorviz.landscape.model.helper.BaseEntity;

/**
 * Model representing executed database queries.
 */
@SuppressWarnings("serial")
@Type("databasequery")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "super.id")
public class DatabaseQuery extends BaseEntity {

  private long timestamp;
  // at the moment: Statement or PreparedStatement
  private String statementType;
  private String sqlStatement;
  private String returnValue;
  private long responseTime;

  @Relationship("parentApplication")
  private Application parentApplication;

  @JsonCreator
  public DatabaseQuery(@JsonProperty("id") final String id) {
    super(id);
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(final long timestamp) {
    this.timestamp = timestamp;
  }

  public String getStatementType() {
    return this.statementType;
  }

  public void setStatementType(final String statementType) {
    this.statementType = statementType;
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

  public Application getParentApplication() {
    return this.parentApplication;
  }

  public void setParentApplication(final Application parentApplication) {
    this.parentApplication = parentApplication;
  }

}
