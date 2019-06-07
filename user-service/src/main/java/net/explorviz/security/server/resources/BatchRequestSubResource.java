package net.explorviz.security.server.resources;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import net.explorviz.security.model.UserBatchRequest;
import net.explorviz.security.services.BatchCreationService;
import net.explorviz.security.services.DuplicateUserException;
import net.explorviz.security.services.UserCrudException;
import net.explorviz.shared.security.model.User;

/**
 * Resource that handle batch creation requests.
 *
 */
public class BatchRequestSubResource {

  private static final String ADMIN_ROLE = "admin";
  private static final String MEDIA_TYPE = "application/vnd.api+json";


  private final BatchCreationService bcs;

  @Inject
  public BatchRequestSubResource(final BatchCreationService batchCreationService) {
    this.bcs = batchCreationService;
  }

  /**
   * Creates all users in a list.
   * 
   * @param batch a {@link UserBatchRequest} that defines the users to create
   * @return a list of users objects, that were saved
   */
  @POST
  @Consumes(MEDIA_TYPE)
  @Produces(MEDIA_TYPE)
  @RolesAllowed({ADMIN_ROLE})
  public List<User> batchCreate(final UserBatchRequest batch) {
    System.out.println(batch);
    if (batch.getCount() == 0) {
      throw new BadRequestException("Count must be bigger than 0");
    }
    if (batch.getPrefix() == null || batch.getPrefix().isEmpty()) {
      throw new BadRequestException("Prefix can't be empty");
    }
    if (batch.getPasswords() == null || batch.getPasswords().size() != batch.getCount()) {
      throw new BadRequestException("Passwords must match size of users to create");
    }

    List<User> created = new ArrayList<>();
    try {
      created = this.bcs.create(batch);
    } catch (final DuplicateUserException e) {
      throw new BadRequestException(
          "At least one of the users to create already exists. No user was created");
    } catch (final UserCrudException e) {
      throw new InternalServerErrorException();
    }

    return created;
  }

}
