package net.explorviz.resources

import javax.inject.Inject
import javax.ws.rs.core.MediaType
import javax.ws.rs.Produces
import javax.ws.rs.GET
import javax.ws.rs.Path
import net.explorviz.server.repository.LandscapeRepositoryModel
import javax.ws.rs.PathParam
import net.explorviz.server.security.Secured
import com.github.jasminb.jsonapi.ResourceConverter
import net.explorviz.model.Landscape
import com.github.jasminb.jsonapi.SerializationFeature
import com.github.jasminb.jsonapi.JSONAPIDocument
import net.explorviz.layout.LayoutService
import net.explorviz.model.TestA
import net.explorviz.model.TestB

@Path("landscapes")
class LandscapeResource {

	var LandscapeRepositoryModel service

	@Inject
	def LandscapeResource(LandscapeRepositoryModel service) {
		this.service = service
	}

	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/{landscapeId}")
	def byte[] getLandscape(@PathParam("landscapeId") String landscapeId) {

		var landscape = LayoutService.layoutLandscape(service.lastPeriodLandscape)

		// test akr //
		var ResourceConverter converter2 = new ResourceConverter(TestA, TestB)
		converter2.enableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES)

		var testa = new TestA
		testa.id = "1"
		testa.title = "ok"

		var testb = new TestB
		testb.id = "2"
		testb.title = "why"
		testb.parent = testa

		testa.articles.add(testb)

		var JSONAPIDocument<TestA> document2 = new JSONAPIDocument<TestA>(testa)

		converter2.writeDocument(document2)

		// end //
		
		
		
		// test 2 akr //
		var ResourceConverter converter = new ResourceConverter(Landscape, System)
		converter.enableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES)

		var JSONAPIDocument<Landscape> document = new JSONAPIDocument<Landscape>(landscape)

		converter.writeDocument(document)

		// end //
		
		
		// test 3 akr //
		var ResourceConverter converter3 = new ResourceConverter(net.explorviz.model.System)
		converter.enableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES)
		
		var system = new net.explorviz.model.System
		system.id = "2"

		var JSONAPIDocument<net.explorviz.model.System> document3 = new JSONAPIDocument<net.explorviz.model.System>(system)

		converter3.writeDocument(document3)

		// end //
		
	}
}
