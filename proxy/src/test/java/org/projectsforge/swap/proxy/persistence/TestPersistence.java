package org.projectsforge.swap.proxy.persistence;

import java.net.URL;
import junit.framework.Assert;
import org.junit.Test;
import org.projectsforge.swap.proxy.TestClientRemoting;
import org.projectsforge.swap.proxy.persistence.data.Data;
import org.projectsforge.swap.proxy.persistence.repository.PersistenceTestingDataRepository;
import org.projectsforge.swap.proxy.starter.ProxyEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPersistence {

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(TestClientRemoting.class);

  @Test
  public void test() throws Exception {
    final ProxyEnvironment environment = new ProxyEnvironment("Test SWAP client");

    try {
      environment.start();

      // SimpleJpaRepository

      final PersistenceTestingDataRepository repository = environment.getContext().getBean(
          PersistenceTestingDataRepository.class);
      Assert.assertEquals(0, repository.findAll().size());
      repository.saveAndFlush(new Data(1L, new URL("http://xxx.fr")));
      repository.saveAndFlush(new Data(1L, new URL("http://yyy.fr")));
      final Data result = repository.saveAndFlush(new Data(null, new URL("http://www.fr")));
      Assert.assertNotNull(result);
      Assert.assertEquals(2L, (long) result.getId());
      repository.saveAndFlush(new Data(3L, new URL("http://zzz.fr")));
      repository.saveAndFlush(new Data(4L, new URL("http://aaa.fr")));
      repository.save(new Data(5L, new URL("http://bbb.fr")));
      repository.save(new Data(6L, new URL("http://ccc.fr")));
      repository.save(new Data(7L, new URL("http://ddd.fr")));
      repository.save(new Data(8L, new URL("http://eee.fr")));
      repository.save(new Data(9L, new URL("http://fff.fr")));
      repository.save(new Data(10L, new URL("http://ggg.fr")));
      repository.save(new Data(11L, new URL("http://hhh.fr")));
      Assert.assertEquals(11, repository.findAll().size());

      /** naming strategy */

    } catch (final Exception e) {
      logger.error("An error occurred", e);
      throw e;
    } finally {
      try {
        environment.stop();
      } catch (final Exception e) {
        logger.info("An error occurred", e);
      }
    }
  }
}
