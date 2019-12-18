package net.explorviz.settings.services;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.services.validation.PreferenceValidationException;
import net.explorviz.settings.services.validation.PreferenceValidator;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a helper service for accessing preferences of specific users.
 *
 */
@Service
public class UserPreferenceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserPreferenceService.class);

  private final UserPreferenceRepository prefRepo;
  private final SettingsRepository settingRepo;


  /**
   * Creates a new service.
   *
   * @param preferenceRepo the repository for user preferences
   */
  @Inject
  public UserPreferenceService(final UserPreferenceRepository preferenceRepo,
      final SettingsRepository settingsRepo) {
    super();
    this.prefRepo = preferenceRepo;
    this.settingRepo = settingsRepo;
  }


  /**
   * Returns a list of {@link UserPreference} object that are associated to the user with the given
   * id.
   *
   * @param userId Id of the user
   */
  public List<UserPreference> getPreferencesForUser(final String userId) {
    return this.prefRepo.findAll()
        .stream()
        .filter(c -> c.getUserId().equals(userId))
        .collect(Collectors.toList());
  }


  /**
   * Checks whether a given custom setting fulfills all constrained of the associated setting.
   *
   * @param customSetting the setting to check
   * @throws PreferenceValidationException if the setting is invalid
   *
   */
  public void validate(final UserPreference customSetting) throws PreferenceValidationException {
    // Retrieve associated setting
    final Setting s = this.settingRepo.find(customSetting.getSettingId())
        .orElseThrow(() -> new PreferenceValidationException(
            String.format("Setting with id %s not found", customSetting.getSettingId())));

    // Validate preference against the settings
    final PreferenceValidator validator = new PreferenceValidator(customSetting);
    validator.validate(s);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(String.format("Validation successful for preference with id %s",
          customSetting.getId().toString()));
    }
  }

}
