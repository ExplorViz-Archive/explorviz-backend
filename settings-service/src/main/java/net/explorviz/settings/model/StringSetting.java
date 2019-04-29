package net.explorviz.settings.model;

import com.github.jasminb.jsonapi.annotations.Type;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Type("stringsetting")
public class StringSetting extends Setting<String> {

  /**
   * {@inheritDoc}
   */
  public StringSetting(final String id, final String name, final String description,
      final String defaultValue, final String origin) {
    super(id, name, description, defaultValue, origin);
  }

  public StringSetting() {
    super();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append(this.getId()).append(this.getName())
        .append(this.getDescription()).append(this.getDefaultValue()).build();
  }

}
