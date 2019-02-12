package net.explorviz.shared.security.model.settings;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

@Type("settingdescriptor")
public abstract class SettingDescriptor<T> {


  private final String description;

  @Id
  private final String idName;

  private final String simpleName;

  private final T defaultValue;

  public SettingDescriptor(final String id, final String name, final String description,
      final T defaultValue) {
    this.idName = id;
    this.simpleName = name;
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
    return this.simpleName;
  }



}
