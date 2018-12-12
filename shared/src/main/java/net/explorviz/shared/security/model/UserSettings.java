package net.explorviz.shared.security.model;

import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

/**
 * Model class for the user settings in the frontend.
 */
@Type("usersetting")
public class UserSettings {

  @Id(LongIdHandler.class)
  private Long id = 1L;

  private boolean showFpsCounter;

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

  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof UserSettings)) {
      return false;
    }
    final UserSettings otherObj = (UserSettings) obj;

    return this.id.equals(otherObj.getId())
        && this.appVizClassColor.equals(otherObj.appVizClassColor)
        && this.showFpsCounter == otherObj.isShowFpsCounter();
  }



}
