package net.explorviz.shared.security.model.settings;

import com.github.jasminb.jsonapi.annotations.Type;

@Type("settingdescriptor")
public abstract class SettingDescriptor<T> {


  private final String description;

  private final String name;

  private final T defaultValue;

  public SettingDescriptor(final String name, final String description, final T defaultValue) {
    this.name = name;
    this.description = description;
    this.defaultValue = defaultValue;
  }


  public String getDescription() {
    return this.description;
  }

  public T getDefaultValue() {
    return this.defaultValue;
  }

  public String getName() {
    return this.name;
  }



}
