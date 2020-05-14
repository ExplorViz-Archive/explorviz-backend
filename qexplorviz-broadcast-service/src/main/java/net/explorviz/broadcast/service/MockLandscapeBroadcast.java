package net.explorviz.broadcast.service;

import javax.ws.rs.sse.SseEventSink;


/**
 * Temporary Mock for broadcasts while issues unresolved.
 * All operations are no-ops.
 */
public class MockLandscapeBroadcast implements SseBroadcast<String> {


  @Override
  public void broadcast(final String message) {
    System.out.println(message); // NOPMD
  }

  @Override
  public void register(final SseEventSink sink) {
    // NOPMD
  }
}
