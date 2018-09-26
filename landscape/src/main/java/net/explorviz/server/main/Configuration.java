package net.explorviz.server.main;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Configuration settings for the backend.
 */
public class Configuration {
  public static int outputIntervalSeconds = 10; // NOCS
  public static final List<String> DATABASE_NAMES = new ArrayList<>();
  public static final int TIMESHIFT_INTERVAL_IN_MINUTES = 10;
  public static final int HISTORY_INTERVAL_IN_MINUTES = 24 * 60; // one day
  public static final String MODEL_EXTENSION = ".expl";
  public static final String LANDSCAPE_REPOSITORY = "landscapeRepository";
  public static final String REPLAY_REPOSITORY = "replayRepository";

  // ENABLE_KIEKER_ADAPTER allows the usage of Kieker as monitoring component
  public static final boolean ENABLE_KIEKER_ADAPTER = true;
}
