package net.explorviz.shared.security.model.settings;

public abstract class SettingObject<T> {

  private final T value;

  private final String description;

  private final String name;

  public SettingObject(final String name, final T value, final String description) {
    this.value = value;
    this.name = name;
    this.description = description;
  }

  public T getValue() {
    return this.value;
  }

  public String getDescription() {
    return this.description;
  }

  public String getName() {
    return this.name;
  }



}
