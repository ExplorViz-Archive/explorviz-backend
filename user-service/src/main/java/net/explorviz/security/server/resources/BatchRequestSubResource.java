package net.explorviz.security.server.resources;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import net.explorviz.security.model.UserBatchRequest;
import net.explorviz.security.services.BatchCreationService;
import net.explorviz.security.services.exceptions.DuplicateUserException;
import net.explorviz.security.services.exceptions.MalformedBatchRequestException;
import net.explorviz.security.services.exceptions.UserCrudException;
import net.explorviz.shared.security.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource that handle batch creation requests.
 *
 */
public class BatchRequestSubResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchRequestSubResource.class);

  private static final String ADMIN_ROLE = "admin";
  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final int MAX_COUNT = 300;


  private final BatchCreationService bcs;

  @Inject
  public BatchRequestSubResource(final BatchCreationService batchCreationService) {
    this.bcs = batchCreationService;
  }

  /**
   * Creates all users in a list.
   *
   * @param batch a {@link UserBatchRequest} that defines the users to create
   * @return the given batch object including a list of users objects that were saved
   */
  @POST
  @Consumes(MEDIA_TYPE)
  @Produces(MEDIA_TYPE)
  @RolesAllowed({ADMIN_ROLE})
  public UserBatchRequest batchCreate(@Context final HttpHeaders headers,
      final UserBatchRequest batch) {
    try {
      if (batch.getCount() > MAX_COUNT) {
        throw new MalformedBatchRequestException("Count must be smaller than " + MAX_COUNT);
      }

      if (batch.getCount() == 0) {
        throw new MalformedBatchRequestException("Count must be bigger than 0");
      }
      if (batch.getPrefix() == null || batch.getPrefix().isEmpty()) {
        throw new MalformedBatchRequestException("Prefix can't be empty");
      }
      if (batch.getPasswords() == null || batch.getPasswords().size() != batch.getCount()) {
        throw new MalformedBatchRequestException("Passwords must match size of users to create");
      }

      final List<User> created =
          this.bcs.create(batch, headers.getHeaderString(HttpHeaders.AUTHORIZATION));

      batch.setUsers(created);

      return batch;

    } catch (final DuplicateUserException e) {
      throw new BadRequestException(
          "At least one of the users to create already exists. No user was created");
    } catch (final MalformedBatchRequestException e) {
      LOGGER.error(e.getMessage());
      throw new BadRequestException(e.getMessage());
    } catch (final UserCrudException e) {
      LOGGER.error(e.getMessage());
      throw new InternalServerErrorException("No user created.");
    }


  }

}
