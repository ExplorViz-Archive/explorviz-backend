package net.explorviz.broadcast.service;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
public class ServiceConfiguration {

  @Produces
  public SseBroadcast<String> StringBroadcaster() {
    return new MockLandscapeBroadcast();
  }
  
}
