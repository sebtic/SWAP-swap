/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This
 * software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This software is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this software. If not, see
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.proxy;

import org.junit.Assert;
import org.junit.Test;
import org.projectsforge.swap.proxy.starter.ProxyEnvironment;
import org.projectsforge.swap.proxy.test.remoting.MagicNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.RemoteAccessException;

/**
 * Unit test the remoting.
 * 
 * @author Sébastien Aupetit
 */
public class TestClientRemoting {

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(TestClientRemoting.class);

  /**
   * Test remoting.
   * 
   * @throws Exception the exception
   */
  @Test
  public void testRemoting() throws Exception {
    final ProxyEnvironment environment = new ProxyEnvironment("Test SWAP client");

    if ("1".equals(System.getProperty("swap.tests.remoting"))) {

      try {
        environment.start();

        try {
          final MagicNumber mn = environment.getContext().getBean(MagicNumber.class);
          Assert.assertEquals(mn.getMagicNumber(), 1234);
        } catch (final RemoteAccessException e) {
          logger.error("Remoting call failed", e);
          Assert.fail("Remoting call failed");
        }

      } finally {
        try {
          environment.stop();
        } catch (final Exception e) {
          logger.info("An error occurred", e);
        }
      }
    } else {
      logger
          .warn("Remoting tests not enabled. Define swap.tests.remoting system property to 1 to active.");
    }
  }
}
