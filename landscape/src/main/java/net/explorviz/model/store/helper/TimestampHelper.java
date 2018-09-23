package net.explorviz.model.store.helper;

import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import net.explorviz.model.store.Timestamp;

/**
 * Helper class providing methods for filtering lists of timestamps.
 */
public class TimestampHelper {

  /**
   * Retrieves timestamps BEFORE a passed timestamp.
   *
   * @param allTimestamps - All timestamps within the system
   * @param fromTimestamp - Define the timestamp which sets the limit
   * @param intervalSize - The number of retrieved timestamps
   * @return List of Timestamp
   */
  public static List<Timestamp> filterTimestampsBeforeTimestamp(final List<Timestamp> allTimestamps,
      final long fromTimestamp, final int intervalSize) {

    final int length = allTimestamps.size();

    int position = 0;
    // iterate backwards and find passed timestamp
    for (int i = length; i > 0; i--) {

      final Timestamp element = allTimestamps.get(i);

      if (element.getId().equals(fromTimestamp)) {
        position = allTimestamps.indexOf(element);

        try {
          if (intervalSize != 0) {
            if (position - intervalSize < 0) {
              return allTimestamps.subList(0, position);
            } else {
              return allTimestamps.subList(position - intervalSize, position);
            }
          } else {
            // all timestamps starting at position
            return allTimestamps.subList(0, position);
          }

        } catch (final IllegalArgumentException e) {
          throw new WebApplicationException(e);
        }

      }
    }
    return new LinkedList<Timestamp>();
  }

  /**
   * Retrieves timestamps AFTER a passed timestamp.
   *
   * @param allTimestamps - All timestamps within the system
   * @param afterTimestamp - Define the timestamp which sets the limit
   * @param intervalSize - The number of retrieved timestamps
   * @return List of Timestamp
   */
  public static List<Timestamp> filterTimestampsAfterTimestamp(final List<Timestamp> allTimestamps,
      final long afterTimestamp, final int intervalSize) {

    final int length = allTimestamps.size();

    int position = 0;
    // iterate backwards and find passed timestamp
    for (int i = length; i > 0; i--) {

      final Timestamp element = allTimestamps.get(i);

      if (element.getId().equals(afterTimestamp)) {
        position = allTimestamps.indexOf(element);

        try {
          if (intervalSize != 0) {
            if (position + intervalSize > length) {
              return allTimestamps.subList(position, length);
            } else {
              return allTimestamps.subList(position, position + intervalSize);
            }
          } else {
            // all timestamps starting at position
            return allTimestamps.subList(position, length);
          }

        } catch (final IllegalArgumentException e) {
          throw new WebApplicationException(e);
        }
      }
    }
    return new LinkedList<Timestamp>();
  }

  /**
   * Retrieves the oldest timestamps in a passed interval.
   *
   * @param allTimestamps - All timestamps within the system
   * @param intervalSize - The number of retrieved timestamps
   * @return List of Timestamp
   */
  public static List<Timestamp> filterOldestTimestamps(final List<Timestamp> allTimestamps,
      final int intervalSize) {

    if (allTimestamps.isEmpty()) {
      return new LinkedList<Timestamp>();
    }

    final int length = allTimestamps.size();

    try {
      if (intervalSize != 0) {
        if (intervalSize >= length) {
          return allTimestamps.subList(0, length);
        } else {
          return allTimestamps.subList(0, intervalSize);
        }

      } else {
        return allTimestamps;
      }

    } catch (final IllegalArgumentException e) {
      throw new WebApplicationException(e);
    }
  }

  /**
   * Retrieves the newest timestamps in a passed interval.
   *
   * @param allTimestamps - All timestamps within the system
   * @param intervalSize - The number of retrieved timestamps
   * @return List of Timestamp
   */
  public static List<Timestamp> filterMostRecentTimestamps(final List<Timestamp> allTimestamps,
      final int intervalSize) {

    if (allTimestamps.isEmpty()) {
      return new LinkedList<Timestamp>();
    }

    final int length = allTimestamps.size();

    try {
      if (intervalSize != 0) {
        if (intervalSize >= length) {
          return allTimestamps.subList(0, length);
        } else {
          return allTimestamps.subList(length - intervalSize, length);
        }
      } else {
        return allTimestamps;
      }

    } catch (final IllegalArgumentException e) {
      throw new WebApplicationException(e);
    }
  }

}
