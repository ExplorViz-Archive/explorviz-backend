package net.explorviz.settings.services;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.explorviz.settings.model.CustomSetting;
import org.jvnet.hk2.annotations.Service;

@Service
public class CustomSettingsService {


  private final CustomSettingsRepository customRepo;

  @Inject
  public CustomSettingsService(final CustomSettingsRepository customRepo) {
    super();
    this.customRepo = customRepo;
  }


  /**
   * Returns a list of {@link CustomSetting} object that are associated to the user with the given
   * id.
   *
   * @param userId Id of the user
   */
  public List<CustomSetting> getCustomsForUser(final String userId) {
    return this.customRepo.findAll().stream().filter(c -> c.getUserId().equals(userId))
        .collect(Collectors.toList());
  }


  /**
   * Checks whether a given custom setting fulfills all constrained of the associated setting.
   *
   * @throws SettingValidationException if the setting is invalid
   * @param customSetting the setting to check
   */
  public void validate(final CustomSetting customSetting) throws SettingValidationException {
    // TODO
  }

}
