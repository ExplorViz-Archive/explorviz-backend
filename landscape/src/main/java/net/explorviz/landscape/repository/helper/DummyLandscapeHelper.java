package net.explorviz.landscape.repository.helper;

import java.sql.Timestamp;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Helper class providing methods for creating a dummy landscape.
 */
public final class DummyLandscapeHelper {

  private DummyLandscapeHelper() {
    // Utility Class
  }

  /**
   * Returns the current timestamp for dummy queries.
   *
   * @return current timestamp
   */
  public static long getCurrentTimestamp() {
    final Timestamp timestamp = new Timestamp(System.nanoTime());
    return timestamp.getTime();
  }

  /**
   * Returns a random number between minValue and maxValue.
   *
   * @param minValue - Minimum random value
   * @param maxValue - Maximum random value
   * @return random number
   */
  public static int getRandomNum(final int minValue, final int maxValue) {
    if (minValue > 0 && maxValue > 0) {
      return ThreadLocalRandom.current().nextInt(minValue, maxValue + 1);
    }
    return 0;
  }

  /**
   * Returns a unique next sequence number.
   *
   * @return Next unique number
   */
  public static int getNextSequenceId() {
    final AtomicInteger seqId = new AtomicInteger();
    return seqId.getAndIncrement();
  }

}
