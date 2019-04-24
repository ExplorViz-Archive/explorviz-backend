package net.explorviz.settings.model;

import com.github.jasminb.jsonapi.annotations.Type;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Type("booleansetting")
public class BooleanSetting extends Setting<Boolean> {


  /**
   * {@inheritDoc}
   */
  public BooleanSetting(String id, String name, String description, Boolean defaultValue,
      String origin) {
    super(id, name, description, defaultValue, origin);
    // TODO Auto-generated constructor stub
  }


  public BooleanSetting() {
    super();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append(this.getId()).append(this.getName())
        .append(this.getDescription()).append(this.getDefaultValue()).build();
  }



}
