package net.explorviz.broadcast.service;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

/**
 * Dependency configuration for services.
 */
@Dependent
public class ServiceConfiguration {

  @Produces
  public SseBroadcast<String> stringBroadcaster() {
    return new MockLandscapeBroadcast();
  }
  
}
