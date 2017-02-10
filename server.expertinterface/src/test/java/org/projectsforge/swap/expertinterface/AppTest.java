package org.projectsforge.swap.expertinterface;

import java.util.List;

import org.projectsforge.swap.core.expertinterface.Page;
import org.projectsforge.swap.core.expertinterface.Zone;
import org.projectsforge.swap.core.expertinterface.ZoneTable;
import org.projectsforge.swap.server.expertinterface.ZonesRepository;
import org.projectsforge.swap.server.starter.ServerEnvironment;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(AppTest.class);
  }

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
   * Test JPA inheritance
   *
   * @throws Exception
   */
  public void testRepository() throws Exception {
    final ServerEnvironment environment = new ServerEnvironment("Test SWAP server");

    try {
      environment.start();

      ZonesRepository repository = environment.getContext().getBean(ZonesRepository.class);

      assertNotNull(repository);

      Page page1 = new Page();
      page1.setHtml("<b>foo</b>");

      Zone zone1 = new Zone();
      zone1.setLabel("simplezone1");
      zone1.setPage(page1);
      zone1.setContent("foo");
      zone1.setUrl("https://truc");
      zone1.setXpath("foo");
      assertNotNull(repository.saveAndFlush(zone1));

      Page page2 = new Page();
      page2.setHtml("<b>foo</b>");

      Zone zone2 = new Zone();
      zone2.setLabel("simplezone1");
      zone2.setPage(page2);
      zone2.setContent("foo");
      zone2.setUrl("https://truc");
      zone2.setXpath("foo");
      assertNotNull(repository.saveAndFlush(zone2));

      Page page3 = new Page();
      page3.setHtml("<b>foo</b>");

      ZoneTable zone3 = new ZoneTable();
      zone3.setLabel("simplezone2");
      zone3.setSubtype("deco");
      zone3.setPage(page3);
      zone3.setContent("foo");
      zone3.setUrl("https://truc");
      zone3.setXpath("foo");
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
