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
 * $Id$
 */
package org.projectsforge.swap.core.environment.startermonitor;

import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.environment.StarterMonitor;

/**
 * A {@link StarterMonitor} which log the actions.
 * 
 * @author Sébastien Aupetit
 */
public class LoggerMonitorStarter implements StarterMonitor {

  /** The logger. */
  private org.slf4j.Logger logger;

  /** The environment. */
  private Environment environment;

  /** The count. */
  private int count;

  /** The log format. */
  private String logFormat;

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.configurer.StarterMonitor#done()
   */
  @Override
  public void done() {
    logger.info("{} started", environment.getName());
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.configurer.StarterMonitor#nextStep(java.lang
   * .String)
   */
  @Override
  public void nextStep(final String message) {
    count++;
    logger.info(logFormat, count, message);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.configurer.StarterMonitor#setEnvironment(org
   * .projectsforge.swap.core.configurer.Environment)
   */
  @Override
  public void setEnvironment(final Environment environment) {
    this.environment = environment;
    logger = environment.getLogger();
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.configurer.StarterMonitor#setMaxStep(int)
   */
  @Override
  public void setMaxStep(final int maxStep) {
    this.count = 0;
    logFormat = "[{}/" + maxStep + "] {}";
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.configurer.StarterMonitor#updateStatus(java
   * .lang.String)
   */
  @Override
  public void updateStatus(final String message) {
    logger.debug(logFormat, count, message);
  }

}
