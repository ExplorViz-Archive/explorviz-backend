package net.explorviz.security.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple {@link IdGenerator}} that creates ids by counting.
 *
 */
public class CountingIdGenerator implements IdGenerator<Long> {


  private final AtomicLong lastId;

  /**
   * Creates a new {@link CountingIdGenerator} that will start generating ids from 1.
   */
  public CountingIdGenerator() {
    this(0L);
  }

  /**
   * Creates a new {@link CountingIdGenerator} that will skip the first {@code skip} numbers. I.e.
   * that first generated id will be {@code skip+1}.
   *
   * @param skip the numbers to skip
   */
  public CountingIdGenerator(final Long skip) {
    this.lastId = new AtomicLong(skip);
  }


  @Override
  public Long next() {
    synchronized (this) {
      return this.lastId.incrementAndGet();
    }
  }



}
