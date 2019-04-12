package net.explorviz.settings.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class StringSetting extends Setting<String>{

  /**
   * {@inheritDoc}
   */
  public StringSetting(String id, String name, String description, String defaultValue) {
    super(id, name, description, defaultValue);
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
