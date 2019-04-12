package net.explorviz.settings.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class BooleanSetting extends Setting<Boolean>{

 
  /**
   * {@inheritDoc}
   */
  public BooleanSetting(String id, String name, String description, Boolean defaultValue) {
    super(id, name, description, defaultValue);
    // TODO Auto-generated constructor stub
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append(this.getId())
        .append(this.getName())
        .append(this.getDescription())
        .append(this.getDefaultValue())
        .build();
  }

  
  
}
