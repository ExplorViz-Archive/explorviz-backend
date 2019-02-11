package net.explorviz.landscape.model.store.helper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import net.explorviz.landscape.model.store.Timestamp;

/**
 * Helper class providing methods for filtering lists of {@link Timestamp}.
 */
public final class TimestampHelper {

  private TimestampHelper() {
    // Utility Class
  }

  /**
   * Retrieves timestamps AFTER a passed {@link net.explorviz.landscape.model.store.Timestamp}.
   *
   * @param allTimestamps - All timestamps within the system
   * @param afterTimestamp - Define the timestamp which sets the limit
   * @param intervalSize - The number of retrieved timestamps
   * @return List of Timestamp
   */
  public static List<Timestamp> filterTimestampsAfterTimestamp(final List<Timestamp> allTimestamps,
      final long afterTimestamp, final int intervalSize) {

    final int timestampListSize = allTimestamps.size();

    // search the passed timestamp
    final Timestamp foundTimestamp = getTimestampPosition(allTimestamps, afterTimestamp);
    final int foundTimestampPosition = allTimestamps.indexOf(foundTimestamp);

    // no timestamp was found
    if (foundTimestampPosition == -1) {
      return new LinkedList<>();
    } else {
      try {
        if (intervalSize == 0 || intervalSize > timestampListSize) {
          // all timestamps starting at position
          return allTimestamps.subList(foundTimestampPosition, timestampListSize);
        } else {
          if (foundTimestampPosition + intervalSize > timestampListSize) {
            return allTimestamps.subList(foundTimestampPosition, timestampListSize);
          } else {
            return allTimestamps.subList(foundTimestampPosition,
                foundTimestampPosition + intervalSize);
          }
        }
      } catch (final IllegalArgumentException e) {
        throw new WebApplicationException(e);
      }
    }
  }

  /**
   * Retrieves the a passed {@link Timestamp} within a list of timestamps if found, otherwise the
   * following timestamp.
   *
   * @param timestamps - a list of timestamps
   * @param searchedTimestamp - a specific timestamp to be found
   * @return a retrieved timestamp
   */
  public static Timestamp getTimestampPosition(final List<Timestamp> timestamps,
      final long searchedTimestamp) {

    final Iterator<Timestamp> iterator = timestamps.iterator();

    while (iterator.hasNext()) {

      final Timestamp currentTimestamp = iterator.next();

      // searched timestamp found
      if (currentTimestamp.getTimestamp() == searchedTimestamp) {
        return currentTimestamp;
      }
      // next timestamp later than searched timestamp found
      else if (currentTimestamp.getTimestamp() > searchedTimestamp) {
        return currentTimestamp;
      }
    }
    return new Timestamp();
  }

}
