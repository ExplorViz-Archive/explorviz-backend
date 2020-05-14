package net.explorviz.broadcast.service;

import javax.ws.rs.sse.SseEventSink;

/**
 * Provides methods that can be used to broadcast SSEs.
 * 
 * @param <T> the type of broadcast messages
 */
public interface SseBroadcast<T> {

  void broadcast(final T message);

  void register(final SseEventSink sink);

}
