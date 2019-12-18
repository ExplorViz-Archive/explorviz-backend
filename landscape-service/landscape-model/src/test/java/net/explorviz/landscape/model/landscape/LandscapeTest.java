package net.explorviz.landscape.model.landscape;



import static org.junit.jupiter.api.Assertions.assertEquals;

import net.explorviz.landscape.model.event.EEventType;
import net.explorviz.landscape.model.event.Event;
import net.explorviz.landscape.model.store.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LandscapeTest {

  private Landscape l;

  @BeforeEach
  public void setUp() {
    this.l = new Landscape("1", new Timestamp("2", 1556558138878L, 300));
  }

  /**
   * Check if newly created event is really added to landscape.
   *
   * @see Landscape
   */
  @Test
  public void testEventCreation() {
    // declare the expected new application event
    final long currentMillis = java.lang.System.currentTimeMillis();

    final String expectedEventMessage = "New application 'jPetStore' on node 'node1' detected";
    final Event expectedEvent =
        new Event("3", currentMillis, EEventType.NEWAPPLICATION, expectedEventMessage);

    // test the method and verify
    this.l.createNewEvent("3", EEventType.NEWAPPLICATION, expectedEventMessage);

    final Event actualEvent = this.l.getEvents().get(0);

    // the attributes must be equal
    assertEquals(expectedEvent.getEventType(), actualEvent.getEventType());
    assertEquals(expectedEvent.getEventMessage(), actualEvent.getEventMessage());
  }

  /**
   * Tests the creation of a new exception event
   */
  @Test
  public void testCreateNewException() {

    // declare the expected exception event
    final long currentMillis = java.lang.System.currentTimeMillis();

    final String expectedCause =
        "Exception thrown in application 'sampleApplication' by class 'boolean java.sql.Statement.execute(String)':\\n ...";
    final Event expectedEvent = new Event("4", currentMillis, EEventType.EXCEPTION, expectedCause);

    // test the method and verify
    this.l.createNewException("4", expectedCause);

    final Event actualEvent = this.l.getEvents().get(0);

    // the attributes must be equal
    assertEquals(expectedEvent.getEventType(), actualEvent.getEventType());
    assertEquals(expectedEvent.getEventMessage(), actualEvent.getEventMessage());
  }

}
