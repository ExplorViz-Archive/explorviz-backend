package net.explorviz.settings.services;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.explorviz.settings.model.UserPreference;
import org.jvnet.hk2.annotations.Service;

/**
 * This is a helper service for accessing preferences of specific users.
 *
 */
@Service
public class UserPreferenceService {


  private final UserPreferenceRepository customRepo;

  @Inject
  public UserPreferenceService(final UserPreferenceRepository customRepo) {
    super();
    this.customRepo = customRepo;
  }


  /**
   * Returns a list of {@link UserPreference} object that are associated to the user with the given
   * id.
   *
   * @param userId Id of the user
   */
  public List<UserPreference> getCustomsForUser(final String userId) {
    return this.customRepo.findAll().stream().filter(c -> c.getUserId().equals(userId))
        .collect(Collectors.toList());
  }


  /**
   * Checks whether a given custom setting fulfills all constrained of the associated setting.
   *
   * @param customSetting the setting to check
   * @throws SettingValidationException if the setting is invalid
   *
   */
  public void validate(final UserPreference customSetting) {
    // TODO
  }

}
