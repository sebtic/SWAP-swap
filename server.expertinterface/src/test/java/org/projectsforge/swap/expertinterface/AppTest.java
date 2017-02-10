package org.projectsforge.swap.expertinterface;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.projectsforge.swap.core.expertinterface.Zone;
import org.projectsforge.swap.core.expertinterface.ZoneTable;
import org.projectsforge.swap.server.expertinterface.ZonesRepository;
import org.projectsforge.swap.server.starter.ServerEnvironment;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
  /**
   * Create the test case
   * 
   * @param testName
   *          name of the test case
   */
  public AppTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(AppTest.class);
  }

  /**
   * Test JPA inheritance
   * @throws Exception 
   */
  public void testRepository() throws Exception {
    final ServerEnvironment environment = new ServerEnvironment("Test SWAP server");

    try {
      environment.start();

      ZonesRepository repository = environment.getContext().getBean(ZonesRepository.class);
      
      assertNotNull(repository);
      
      Zone zone1 = new Zone();
      zone1.setLabel("simplezone1");
      assertNotNull(repository.saveAndFlush(zone1));
      
      Zone zone2 = new Zone();
      zone2.setLabel("simplezone1");
      assertNotNull(repository.saveAndFlush(zone2));
      
      ZoneTable zone3 = new ZoneTable();
      zone3.setLabel("simplezone2");
      zone3.setSubtype("deco");
      assertNotNull(repository.saveAndFlush(zone3));
      
      List<Zone> zones = repository.findAll();
      assertEquals(3, zones.size());
      ZoneTable zone3r = (ZoneTable) zones.get(2);
      assertNotNull(zone3r);
      assertEquals("deco", zone3r.getSubtype());
      
    } finally {
      environment.stop();
    }
  }
}
