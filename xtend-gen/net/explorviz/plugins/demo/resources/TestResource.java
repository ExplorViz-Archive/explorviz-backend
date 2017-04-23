package net.explorviz.plugins.demo.resources;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.explorviz.model.Landscape;
import net.explorviz.server.repository.LandscapeExchangeService;
import net.explorviz.server.repository.LandscapeRepositoryModel;
import net.explorviz.server.security.Secured;
import org.eclipse.xtext.xbase.lib.Exceptions;

@Secured
@Path("plugin/test")
@SuppressWarnings("all")
public class TestResource {
  private LandscapeRepositoryModel model;
  
  private ResourceConverter converter;
  
  private LandscapeExchangeService service;
  
  @Inject
  public LandscapeExchangeService LandscapeResource(final LandscapeRepositoryModel model, final ResourceConverter converter, final LandscapeExchangeService service) {
    LandscapeExchangeService _xblockexpression = null;
    {
      this.model = model;
      this.converter = converter;
      _xblockexpression = this.service = service;
    }
    return _xblockexpression;
  }
  
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  @Path("/latest-landscape")
  public byte[] getLatestLandscape() {
    try {
      byte[] _xblockexpression = null;
      {
        Landscape landscape = this.model.getLastPeriodLandscape();
        JSONAPIDocument<Landscape> document = new JSONAPIDocument<Landscape>(landscape);
        _xblockexpression = this.converter.writeDocument(document);
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
