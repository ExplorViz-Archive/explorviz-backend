package net.explorviz.settings.model;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import net.bytebuddy.implementation.bind.annotation.Morph;
import xyz.morphia.annotations.Entity;

/**
 * Model of a single setting object. 
 */
@Type("setting")
@Entity("setting")
public class Setting<T> {

  private final String description;

  @Id
  @xyz.morphia.annotations.Id
  private final String idName;

  private final String simpleName;

  private final T defaultValue;
  
  /**
   * Creates a new setting
   * @param id a unique identifier to the setting
   * @param name name of the setting
   * @param description a short description of values and their effects
   * @param defaultValue the default value
   */
  public Setting(final String id, final String name, final String description,
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
  
  public String getId() {
    return this.idName;
  }
  
}
