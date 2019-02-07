package net.explorviz.shared.security.model.settings;

import com.github.jasminb.jsonapi.annotations.Type;

@Type("stringsettingsdescriptor")
public class StringSettingDescriptor extends SettingDescriptor<String> {

  public StringSettingDescriptor(final String id, final String name, final String description,
      final String defaultValue) {
    super(id, name, description, defaultValue);
  }



}
