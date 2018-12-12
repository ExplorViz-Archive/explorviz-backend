package net.explorviz.shared.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

/**
 * Model class for the user settings in the frontend.
 */
@Type("user-settings")
public class UserSettings {

  @Id(LongIdHandler.class)
  private Long id;

  @JsonProperty("show-fps-counter")
  private boolean showFpsCounter;

  @JsonProperty("app-viz-class-color")
  private String appVizClassColor = "0xFF0000";

  public UserSettings() {
    // For MongoDB
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public boolean isShowFpsCounter() {
    return this.showFpsCounter;
  }


  public void setShowFpsCounter(final boolean showFpsCounter) {
    this.showFpsCounter = showFpsCounter;
  }


  public String getAppVizClassColor() {
    return this.appVizClassColor;
  }

  public void setAppVizClassColor(final String appVizClassColor) {
    this.appVizClassColor = appVizClassColor;
  }



}
