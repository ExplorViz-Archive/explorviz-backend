package net.explorviz.server.helper;

import javax.inject.Singleton;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.jvnet.hk2.annotations.Service;

import net.explorviz.model.landscape.Landscape;

@Service
@Singleton
public class BroadcastService {

	private static final MediaType APPLICATION_JSON_API_TYPE = new MediaType("application", "vnd.api+json");

	private final Sse sse;
	private final SseBroadcaster broadcaster;

	public BroadcastService(@Context final Sse sse) {
		this.sse = sse;
		this.broadcaster = sse.newBroadcaster();
	}

	public void broadcastMessage(final Landscape landscape) {
		final OutboundSseEvent event = sse.newEventBuilder().name("message").mediaType(APPLICATION_JSON_API_TYPE)
				.data(landscape).build();

		broadcaster.broadcast(event);
	}

	public void register(final SseEventSink eventSink) {
		this.broadcaster.register(eventSink);
	}

}
