package net.explorviz.settings.model;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import xyz.morphia.annotations.Entity;

/**
 * Model of a single setting object.
 */
@Type("setting")
@Entity("setting")
public class Setting<T> {

  protected String description;

  @Id
  @xyz.morphia.annotations.Id
  protected String id;

  protected String name;

  protected T defaultValue;

  // By default, we use backend as origin
  protected String origin = "backend";

  /**
   * Creates a new setting
   * 
   * @param id a unique identifier to the setting
   * @param name name of the setting
   * @param description a short description of values and their effects
   * @param defaultValue the default value
   */
  public Setting(final String id, final String name, final String description, final T defaultValue,
      String origin) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.defaultValue = defaultValue;
    this.origin = origin;
  }

  public Setting() {
    // TODO Auto-generated constructor stub
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

  public String getId() {
    return this.id;
  }

  public String getOrigin() {
    return origin;
  }

}
