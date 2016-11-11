package net.explorviz.model;

import net.explorviz.model.helper.BaseEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class DatabaseQuery extends BaseEntity {
  @Accessors
  private String SQLStatement;
  
  @Accessors
  private String returnValue;
  
  @Accessors
  private long timeInNanos;
  
  @Pure
  public String getSQLStatement() {
    return this.SQLStatement;
  }
  
  public void setSQLStatement(final String SQLStatement) {
    this.SQLStatement = SQLStatement;
  }
  
  @Pure
  public String getReturnValue() {
    return this.returnValue;
  }
  
  public void setReturnValue(final String returnValue) {
    this.returnValue = returnValue;
  }
  
  @Pure
  public long getTimeInNanos() {
    return this.timeInNanos;
  }
  
  public void setTimeInNanos(final long timeInNanos) {
    this.timeInNanos = timeInNanos;
  }
}
