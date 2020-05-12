package net.explorviz.history.repository.persistence.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.explorviz.landscape.model.store.Timestamp;

/**
 * Contains helper methods to filter and sort collections of {@link Timestamp}s.
 */
public final class TimestampQueryHelper {

  private static final String MUST_BE_POSITIVE_INTEGER = "Timestamp must be positive integer";

  private TimestampQueryHelper() {
    // Utility class
  }

  /**
   * In-place filter the list of timestamps by a time range. Only timestamps within the given
   * time range are preserved.
   *
   * @param timestamps the list of timestamps to filter
   * @param from       Lower limit, must be a UNIX epoch or null if no lower limit should be used
   * @param to         Upper limit, must be a UNIX epoch or null if not upper limit should be used
   * @return the filtered list
   */
  public static List<Timestamp> filterByTimeRange(final List<Timestamp> timestamps,
      final String from,
      final String to) {
    List<Timestamp> result = new ArrayList<>(timestamps);
    if (from != null) {
      final long fromTs = Long.parseLong(from);
      if (fromTs < 0) {
        throw new IllegalArgumentException(MUST_BE_POSITIVE_INTEGER);
      }
      result = timestamps.parallelStream()
          .filter(t -> fromTs <= t.getUnixTimestamp())
          .collect(Collectors.toList());
    }
    if (to != null) {
      final long toTs = Long.parseLong(to);
      if (toTs < 0) {
        throw new IllegalArgumentException(MUST_BE_POSITIVE_INTEGER);
      }
      result = result.parallelStream()
          .filter(t -> toTs >= t.getUnixTimestamp())
          .collect(Collectors.toList());
    }
    return result;
  }


  /**
   * Sorts the list of timestamp descending.
   *
   * @param timestamps timestamps to sort
   */
  public static List<Timestamp> sort(final List<Timestamp> timestamps) {
    final List<Timestamp> result = new ArrayList<>(timestamps);
    // Sort descending
    result.sort((t1, t2) -> {
      if (t1.getUnixTimestamp() == t2.getUnixTimestamp()) {
        return 0;
      }
      return t1.getUnixTimestamp() > t2.getUnixTimestamp() ? -1 : 1;
    });
    return result;
  }


  /**
   * Paginates the collection of timestamp.
   *
   * @param timestamps list of timestamps to paginate
   * @param number     the page index
   * @param size       page size
   * @return the page of the collection according to page number and size
   */
  public static List<Timestamp> paginate(final List<Timestamp> timestamps, final int number,
      final int size) {
    final int pageFrom = Math.min(number * size, timestamps.size());
    final int pageTo = Math.min(size * number + size, timestamps.size());
    return new ArrayList<>(timestamps.subList(pageFrom, pageTo));
  }

}
