package net.explorviz.shared.security.model.settings;

import com.github.jasminb.jsonapi.annotations.Type;

@Type("booleansettingsdescriptor")
public class BooleanSettingDescriptor extends SettingDescriptor<Boolean> {

  public BooleanSettingDescriptor(final String id, final String name, final String description,
      final Boolean defaultValue) {
    super(id, name, description, defaultValue);
  }



}
