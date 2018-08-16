package net.explorviz.server.resources;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSink;

import net.explorviz.server.helper.BroadcastService;

@Singleton
public class LandscapeBroadcastSubResource {

	private final BroadcastService broadcastService;

	@Inject
	public LandscapeBroadcastSubResource(final BroadcastService broadcastService) {
		this.broadcastService = broadcastService;
	}

	// curl -v -X GET http://localhost:8081/v1/landscapes/broadcast/ -H
	// "Content-Type:
	// text/event-stream"

	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void listenToBroadcast(@Context final SseEventSink eventSink,
			@Context final HttpServletResponse servletResponse) {

		// https://serverfault.com/a/801629
		servletResponse.addHeader("Cache-Control", "no-cache");
		servletResponse.addHeader("X-Accel-Buffering", "no");

		broadcastService.register(eventSink);
	}
}