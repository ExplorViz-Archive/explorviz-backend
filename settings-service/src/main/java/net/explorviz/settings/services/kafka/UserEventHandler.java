package net.explorviz.settings.services.kafka;

/**
 * Handler for user events.
 *
 */
public interface UserEventHandler {

  /**
   * Called each time a user has been deleted.
   *
   * @param userId the id of the deleted user
   */
  void onDelete(String userId);

  /**
   * Called each time a new user was created.
   *
   * @param userId id of the deleted user
   */
  void onCreate(String userId);

}
