package net.explorviz.security.util;

public class CountingIdGenerator implements IdGenerator<Long> {

  // Todo: Set lastId to the highest id, that is currently assigned to a persistent object
  private Long lastId = 1L;

  @Override
  public Long next() {
    return ++this.lastId;
  }


}
