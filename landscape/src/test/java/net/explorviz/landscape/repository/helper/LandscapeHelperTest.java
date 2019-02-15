package net.explorviz.landscape.repository.helper;

import static org.junit.Assert.assertEquals;

import net.explorviz.shared.landscape.model.event.EEventType;
import net.explorviz.shared.landscape.model.event.Event;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link LandscapeHelper} methods.
 *
 */
public class LandscapeHelperTest {

  private Landscape landscape;

  @Before
  public void setUp() {
    this.landscape = new Landscape();
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
    final Event expectedEvent = new Event(currentMillis, EEventType.EXCEPTION, expectedCause);

    // test the method and verify
    LandscapeHelper.createNewException(this.landscape, expectedCause);

    final Event actualEvent = this.landscape.getEvents().get(0);

    // the attributes must be equal
    assertEquals(expectedEvent.getEventType(), actualEvent.getEventType());
    assertEquals(expectedEvent.getEventMessage(), actualEvent.getEventMessage());
  }

  /**
   * Tests the creation of a new event
   */
  @Test
  public void testCreateNewEvent() {

    // declare the expected new application event
    final long currentMillis = java.lang.System.currentTimeMillis();

    final String expectedEventMessage = "New application 'jPetStore' on node 'node1' detected";
    final Event expectedEvent =
        new Event(currentMillis, EEventType.NEWAPPLICATION, expectedEventMessage);

    // test the method and verify
    LandscapeHelper.createNewEvent(this.landscape, EEventType.NEWAPPLICATION, expectedEventMessage);

    final Event actualEvent = this.landscape.getEvents().get(0);

    // the attributes must be equal
    assertEquals(expectedEvent.getEventType(), actualEvent.getEventType());
    assertEquals(expectedEvent.getEventMessage(), actualEvent.getEventMessage());
  }

}

