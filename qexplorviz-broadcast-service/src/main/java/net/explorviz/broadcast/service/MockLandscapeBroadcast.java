package net.explorviz.broadcast.service;

import javax.ws.rs.sse.SseEventSink;


/**
 * Temporary Mock for broadcasts while issues unresolved.
 * All operations are no-ops.
 */
public class MockLandscapeBroadcast implements SseBroadcast<String> {


  @Override
  public void broadcast(String message) {
    System.out.println(message);
  }

  @Override
  public void register(SseEventSink sink) {

  }
}
