package net.explorviz.model.application;

import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.BaseEntity;

/**
 * Model representing executed database queries
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
@SuppressWarnings("serial")
@Type("databasequery")
public class DatabaseQuery extends BaseEntity {

	private long timestamp;
	private String sqlStatement;
	private String returnValue;
	private long responseTime;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public String getSqlStatement() {
		return sqlStatement;
	}

	public void setSqlStatement(final String sqlStatement) {
		this.sqlStatement = sqlStatement;
	}

	public String getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(final String returnValue) {
		this.returnValue = returnValue;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(final long responseTime) {
		this.responseTime = responseTime;
	}

}