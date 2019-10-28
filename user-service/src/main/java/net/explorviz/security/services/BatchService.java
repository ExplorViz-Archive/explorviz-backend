package net.explorviz.security.services;

import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.models.errors.Error;
import com.github.jasminb.jsonapi.models.errors.Errors;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import net.explorviz.security.model.UserBatchRequest;
import net.explorviz.security.services.exceptions.MalformedBatchRequestException;
import net.explorviz.security.services.exceptions.UserCrudException;
import net.explorviz.security.user.User;
import net.explorviz.security.util.PasswordStorage;
import net.explorviz.security.util.PasswordStorage.CannotPerformOperationException;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.common.jsonapi.ResourceConverterFactory;
import net.explorviz.shared.common.provider.JsonApiProvider;
import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.querying.Query;
import net.explorviz.shared.querying.QueryResult;
import org.eclipse.jetty.http.HttpStatus;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper service for batch creation of users.
 *
 */
@Service
public class BatchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchService.class);
  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String HTTP = "http://";
  private static final String MSG_FAIL = "Batch request failed, rolling back";

  private final UserService userService;


  private final ResourceConverter converter;

  private final String settingsServiceHost;
  private final String settingsPrefPath;

  private final IdGenerator idGenerator;

  /**
   * Creates a new service.
   *
   * @param userService instance of {@link UserService}
   * @param converter instance of {@link ResourceConverter}
   * @param settingServiceHost host of the settings service
   */
  @Inject
  public BatchService(final UserService userService, final ResourceConverter converter,
      final IdGenerator idGen, @Config("services.settings") final String settingServiceHost,
      @Config("services.settings.preferences") final String settingsPrefPath) {
    this.userService = userService;
    this.settingsServiceHost = settingServiceHost;
    this.settingsPrefPath = settingsPrefPath;
    this.converter = converter;
    this.idGenerator = idGen;
  }

  /**
   * Creates and persists a set of users.
   *
   * @param batch the batch request
   * @param authHeader of an admin user
   * @return as list of all users created
   * @throws UserCrudException if the batch creation was unsuccessful. If this exception is thrown,
   *         no user is persisted.
   */
  public List<User> create(final UserBatchRequest batch, final String authHeader)
      throws UserCrudException {


    final List<User> createdUsers = new ArrayList<>();
    final List<String> createdPrefs = new ArrayList<>();
    final String batchId = this.idGenerator.generateId();
    batch.setId(batchId);

    for (int i = 0; i < batch.getCount(); i++) {

      User newUser = null;
      try {
        newUser = this
            .newUser(batch.getPrefix(), i, batch.getPasswords().get(i), batch.getRoles(), batchId);
      } catch (final CannotPerformOperationException e1) {
        if (LOGGER.isErrorEnabled()) {
          LOGGER.error(MSG_FAIL);
        }
        this.rollbackUsers(createdUsers);
        throw new UserCrudException("Could not hash password");
      }


      try {
        // Create user and preferences
        final User u = this.userService.saveNewEntity(newUser);
        createdUsers.add(u);
        createdPrefs.addAll(this.createPrefs(u, batch.getPreferences(), authHeader));
      } catch (final UserCrudException e) {
        LOGGER.warn(MSG_FAIL);
        this.rollbackUsers(createdUsers);
        this.rollbackPrefs(createdPrefs, authHeader);
        throw e;
      }
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Created a batch of %d users", createdUsers.size()));
    }
    return createdUsers;
  }


  /**
   * Rolls back previously created users.
   *
   * @param created users to delete
   */
  private void rollbackUsers(final List<User> created) {
    for (final User user : created) {
      try {
        this.userService.deleteEntityById(user.getId());
      } catch (final UserCrudException e) {
        // This should never happen
        if (LOGGER.isErrorEnabled()) {
          LOGGER.error(String.format("Rollback failed for user with id %s", user.getId()), e);
        }
      }
    }
  }

  /**
   * Rolls back all preferences given by dispatching DELETE requests.
   *
   * @param created preferences to delete
   * @param auth http auth header header to authorize at the settings service
   */
  private void rollbackPrefs(final List<String> created, final String auth) {

    if (created == null || created.isEmpty()) {
      return;
    }

    // Initialize client
    final Client c = ClientBuilder.newClient();
    final WebTarget baseTarget =
        c.target(HTTP + this.settingsServiceHost).path(this.settingsPrefPath + "/");
    final MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
    headers.putSingle(HttpHeaders.AUTHORIZATION, auth);

    for (final String id : created) {
      final WebTarget currentTarget = baseTarget.path(id);

      final Invocation.Builder invocationBuilder = currentTarget.request().headers(headers);
      final Invocation i = invocationBuilder.buildDelete();

      final Response r = i.invoke();

      if (r.getStatus() != HttpStatus.NO_CONTENT_204) {
        LOGGER.error("Deletion of a preference failed, id: " + id);
      }
    }
  }


  /**
   * Creates the preferences defined in {@code prefs} for the given user, by dispatching an HTTP
   * request to the Settings-Service.
   *
   * @param user the user to create the preferences for
   * @param prefs the settings with the values
   *
   * @return a list of the ids of the created settings
   *
   * @throws UserCrudException if the creation of a single preference failed
   */
  private List<String> createPrefs(final User user, final Map<String, Object> prefs,
      final String auth) throws UserCrudException {

    final List<String> ids = new ArrayList<>();

    if (prefs == null || prefs.isEmpty()) {
      return ids;
    }

    // Initialize client
    final Client c = ClientBuilder.newBuilder()
        .register(ResourceConverterFactory.class)
        .register(new JsonApiProvider<UserPreference>(this.converter))
        .build();
    final WebTarget target = c.target(HTTP + this.settingsServiceHost).path(this.settingsPrefPath);
    final Invocation.Builder invocationBuilder =
        target.request(MEDIA_TYPE).header(HttpHeaders.AUTHORIZATION, auth);

    System.out.println(HTTP + this.settingsServiceHost + this.settingsPrefPath);

    // Create a request for each entry
    for (final Entry<String, Object> pref : prefs.entrySet()) {
      final UserPreference up =
          new UserPreference("", user.getId(), pref.getKey(), pref.getValue());

      try {
        // perform post request
        final Response r = invocationBuilder.post(Entity.entity(up, MEDIA_TYPE));

        // check if successful and if not throw exception which will result in a rollback

        if (r.getStatus() == HttpStatus.OK_200) {
          // add id of new preference to list
          ids.add(r.readEntity(UserPreference.class).getId());
        } else {
          // delete all prefs created in this call since they won't get returned
          this.rollbackPrefs(ids, auth);
          final Errors errs = r.readEntity(Errors.class);
          // Only throws a single error object
          final Error err = errs.getErrors().get(0);
          if (r.getStatus() == HttpStatus.BAD_REQUEST_400
              || r.getStatus() == HttpStatus.NOT_FOUND_404) {
            throw new MalformedBatchRequestException(err.getDetail());
          } else {
            LOGGER.error("Unkown settings-service error: " + err.getDetail());
            throw new UserCrudException(err.getDetail());
          }
        }
      } catch (final ProcessingException e) {
        // delete all prefs created in this call since they won't get returned
        this.rollbackPrefs(ids, auth);

        // happens if the service is unreachable
        if (e.getCause().getClass().equals(ConnectException.class)) {
          LOGGER.error("Could not create preferences due to unreachable settings-service");
          throw new UserCrudException("Settings-Service unreachable", e);
        } else {
          LOGGER.error("Could not create preferences: ", e);
          throw new UserCrudException("Could not create preferences");
        }
      }
    }

    // return list of all ids of the created preferences
    return ids;
  }

  private User newUser(final String pref, final int num, final String password,
      final List<String> roles, final String batchId) throws CannotPerformOperationException {
    final StringBuilder sb = new StringBuilder();
    final String name = sb.append(pref).append('-').append(num).toString();

    // hash password
    final String hashed = PasswordStorage.createHash(password);


    return new User(null, name, hashed, roles, batchId);
  }


  /**
   * Delets a users that belong to the given batch id.
   * 
   * @param batchId The id of the batch to delete all users of
   */
  public void deleteBatch(String batchId) {
    MultivaluedHashMap<String, String> queryParams = new MultivaluedHashMap<>();
    queryParams.putSingle("filter[batchid]", batchId);
    Query<User> batchQuery = Query.fromParameterMap(queryParams);
    QueryResult<User> res = userService.query(batchQuery);
    LOGGER.info("Delete batch of " + res.getData().size() + " users");
    res.getData().forEach(u -> {
      try {
        this.userService.deleteEntityById(u.getId());
      } catch (final UserCrudException e) {
        LOGGER.warn("Skipped a user during batch deletion: " + e.getMessage());
      }
    });
  }
}
