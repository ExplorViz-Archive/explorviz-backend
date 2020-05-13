package net.explorviz.broadcast.service;

import javax.ws.rs.sse.SseEventSink;

public interface SseBroadcast<T> {

  void broadcast(final T message);

  void register(final SseEventSink sink);

}
