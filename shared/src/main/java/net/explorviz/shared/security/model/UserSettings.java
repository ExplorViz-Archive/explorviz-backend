package net.explorviz.shared.security.model;

import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import javax.ws.rs.BadRequestException;

/**
 * Model class for the user settings in the frontend.
 */
@Type("usersetting")
public class UserSettings {

  @Id(LongIdHandler.class)
  private Long id = 1L;

  private boolean showFpsCounter = false;

  private double appVizCommArrowSize = 1.0;

  private boolean appVizTransparency = true;

  private double appVizTransparencyIntensity = 0.3;

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


  public double getAppVizCommArrowSize() {
    return this.appVizCommArrowSize;
  }

  public void setAppVizCommArrowSize(final double appVizCommArrowSize) {
    this.appVizCommArrowSize = appVizCommArrowSize;
  }


  public boolean isAppVizTransparency() {
    return this.appVizTransparency;
  }

  public void setAppVizTransparency(final boolean appVizTransparency) {
    this.appVizTransparency = appVizTransparency;
  }


  public double getAppVizTransparencyIntensity() {
    return this.appVizTransparencyIntensity;
  }

  public void setAppVizTransparencyIntensity(final double appVizTransparencyIntensity) {
    this.appVizTransparencyIntensity = appVizTransparencyIntensity;
  }

  /*
   * Checks if the settings are valid
   */
  public void validate() {
    if (this.appVizCommArrowSize <= 0.0) {
      throw new BadRequestException("appVizCommArrowSize must be > 0");
    }
    if (this.appVizTransparencyIntensity < 0.0 || this.appVizTransparencyIntensity > 1.0) {
      throw new BadRequestException("appVizCommArrowSize must be between 0.0 and 1.0");
    }
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
        && this.appVizCommArrowSize == otherObj.getAppVizCommArrowSize()
        && this.appVizTransparency == otherObj.isAppVizTransparency()
        && this.appVizTransparencyIntensity == otherObj.getAppVizTransparencyIntensity()
        && this.showFpsCounter == otherObj.isShowFpsCounter();
  }



}
