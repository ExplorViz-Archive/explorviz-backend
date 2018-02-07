package net.explorviz.model;

import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.BaseEntity;

@SuppressWarnings("serial")
@Type("databasequery")
public class DatabaseQuery extends BaseEntity {

	private String sqlStatement;
	private String returnValue;
	private long timeInNanos;

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

	public long getTimeInNanos() {
		return timeInNanos;
	}

	public void setTimeInNanos(final long timeInNanos) {
		this.timeInNanos = timeInNanos;
	}

}