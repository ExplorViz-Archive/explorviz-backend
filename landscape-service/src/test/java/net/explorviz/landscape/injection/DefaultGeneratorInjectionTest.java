package net.explorviz.landscape.injection;


import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;
import net.explorviz.landscape.server.main.DependencyInjectionBinder;
import net.explorviz.shared.common.idgen.AtomicEntityIdGenerator;
import net.explorviz.shared.common.idgen.EntityIdGenerator;
import net.explorviz.shared.common.idgen.ServiceIdGenerator;
import net.explorviz.shared.common.idgen.UuidServiceIdGenerator;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultGeneratorInjectionTest {

  @Inject
  private ServiceIdGenerator serviceIdGen;

  @Inject
  private EntityIdGenerator entityIdGen;

  /**
   * Inject dependencies.
   */
  @BeforeEach
  public void setUp() {
    final AbstractBinder binder = new DependencyInjectionBinder();
    final ServiceLocator locator = ServiceLocatorUtilities.bind(binder);
    locator.inject(this);
  }

  /**
   * Check if injected service generator is (per default) UuidServiceIdGenerator
   *
   * @see UuidServiceIdGenerator
   */
  @Test
  public void testDefaultServiceGeneratorInjection() {
    // TODO use test properties file to check for correct service id generator,
    // e.g. service.generator.redis=true, then check for RedisServiceIdGenerator
    assertTrue(this.serviceIdGen instanceof UuidServiceIdGenerator,
        "Default service generator injection failed. Injected wrong type, expected: '"
            + UuidServiceIdGenerator.class + "', but was: '"
            + this.serviceIdGen.getClass().getName() + "'");
  }

  /**
   * Check if injected service generator is (per default) AtomicEntityIdGenerator
   *
   * @see AtomicEntityIdGenerator
   */
  @Test
  public void testDefaultEntityGeneratorInjection() {
    assertTrue(this.entityIdGen instanceof AtomicEntityIdGenerator,
        "Default entity generator injection failed. Injected wrong type, expected: '"
            + AtomicEntityIdGenerator.class + "', but was: '"
            + this.entityIdGen.getClass().getName() + "'");
  }

}
