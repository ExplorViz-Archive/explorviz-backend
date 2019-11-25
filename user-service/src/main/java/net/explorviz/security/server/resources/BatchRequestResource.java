package net.explorviz.security.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import net.explorviz.security.model.UserBatchRequest;
import net.explorviz.security.services.BatchService;
import net.explorviz.security.services.exceptions.DuplicateUserException;
import net.explorviz.security.services.exceptions.MalformedBatchRequestException;
import net.explorviz.security.services.exceptions.UserCrudException;
import net.explorviz.security.user.Role;
import net.explorviz.security.user.User;
import net.explorviz.shared.security.filters.Secure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource that handle batch creation requests.
 *
 */
@Tags(value = {@Tag(name = "Batch"), @Tag(name = "User")})
@SecurityRequirement(name = "token")
@Secure
@Path("v1/userbatch")
public class BatchRequestResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchRequestResource.class);

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  public static final int MAX_COUNT = 300;


  private final BatchService bcs;

  @Inject
  public BatchRequestResource(final BatchService batchCreationService) {
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
  @RolesAllowed({Role.ADMIN_NAME})
  @Operation(summary = "Create a batch of users with a single request")
  @ApiResponse(responseCode = "200",
      description = "Contains all users created through this batch request. "
          + "All users created by the same batch request have the same value"
          + " for the attribute 'batchId'.",
      content = @Content(mediaType = MEDIA_TYPE,
          schema = @Schema(implementation = UserBatchRequest.class)))
  @ApiResponse(responseCode = "400", description = "Invalid request body or a user already exists. "
      + "In this case, the whole request is rolled back.")
  @RequestBody(
      description = "An object specifying the batch request. "
          + "The 'count' attribute denotes how many users to create."
          + "It must be greater than 0 but smaller than " + MAX_COUNT + ". "
          + "The name of alle users start with the 'prefix' and end with an index "
          + "The prefix thus must not be empty."
          + "The size of the password list must match the amount of users to create. "
          + "The given preferences are valid for all users.",
      content = @Content(schema = @Schema(implementation = UserBatchRequest.class)))
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


  @DELETE
  @Path("/{batch_id}")
  public Response deleteBatch(@PathParam("batch_id") final String batchid) {
    this.bcs.deleteBatch(batchid);

    return Response.noContent().build();
  }


}
