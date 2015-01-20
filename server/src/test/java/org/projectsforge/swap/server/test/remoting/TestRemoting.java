/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SWAP. SWAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SWAP is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SWAP. If not, see
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.server.test.remoting;

import org.junit.Test;
import org.projectsforge.swap.server.starter.ServerEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test the remoting.
 * 
 * @author Sébastien Aupetit
 */
public class TestRemoting {

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(TestRemoting.class);

  /**
   * Test remoting.
   * 
   * @throws Exception the exception
   */
  @Test
  public void testRemoting() throws Exception {
    final ServerEnvironment environment = new ServerEnvironment("Test SWAP server");

    try {
      environment.start();

      if ("1".equals(System.getProperty("swap.tests.remoting"))) {
        final MagicNumberImpl mn = environment.getContext().getBean(MagicNumberImpl.class);
        mn.semaphore.acquire();
      } else {
        logger
            .warn("Remoting tests not enabled. Define swap.tests.remoting system property to 1 to active.");
      }
    } finally {
      environment.stop();
    }
  }
}
