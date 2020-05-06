package net.explorviz.settings.services.kafka;

import java.util.List;
import javax.inject.Inject;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.services.UserPreferenceRepository;
import net.explorviz.settings.services.UserPreferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for user events as received by {@link UserEventConsumer}.
 */
public class UserEventHandlerImpl implements UserEventHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserEventHandlerImpl.class);

  private final UserPreferenceService ups;
  private final UserPreferenceRepository upr;

  @Inject
  public UserEventHandlerImpl(final UserPreferenceService preferenceService,
      final UserPreferenceRepository preferenceRepo) {
    this.ups = preferenceService;
    this.upr = preferenceRepo;
  }

  @Override
  public void onDelete(final String userId) {

    // Delete all preference associated with the deleted user
    final List<UserPreference> prefs = this.ups.getPreferencesForUser(userId);
    prefs.stream().map(UserPreference::getId).forEach(this.upr::delete);
    LOGGER.info(String.format("Deleted %d preferences for user with id %s", prefs.size(), userId));

  }

  @Override
  public void onCreate(final String userId) {
    // Do nothing atm

  }

}
