package net.explorviz.security.server.resources;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import net.explorviz.security.model.UserBatchRequest;
import net.explorviz.security.services.BatchCreationService;
import net.explorviz.shared.security.model.User;

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
   * @param users the list of users to create
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
    if (batch.getPassword() == null || batch.getPassword().isEmpty()) {
      throw new BadRequestException("Password can't be empty");
    }
    final List<User> created = this.bcs.create(batch);

    return created;
  }

}
