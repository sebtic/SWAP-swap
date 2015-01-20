/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SWAP.
 * 
 * SWAP is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SWAP is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SWAP. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id: Main.java 135 2012-05-11 13:36:52Z sebtic $
 */
package org.projectsforge.swap.server.starter;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class of the proxy application.
 * 
 * @author Sébastien Aupetit
 */
public final class Main {

  /** The logger. */
  private static Logger logger = LoggerFactory.getLogger(Main.class);

  /**
   * The main method.
   * 
   * @param args
   *          the arguments
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void main(final String[] args) throws IOException {
    final ServerEnvironment environment = new ServerEnvironment("SWAP server");
    environment.setAllowUserInteraction(false);
    environment.setConfigurationDirectory(new File(System.getProperty("user.home"), ".swapserver"));
    // environment.setStarterMonitor(new GraphicStarterMonitor());
    try {
      environment.start();
    } catch (final Exception e) {
      Main.logger.error("An exception occurred", e);
      environment.stop();
    }
  }
}
