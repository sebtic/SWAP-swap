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
 * A {@link StarterMonitor} which discard all messages.
 * 
 * @author Sébastien Aupetit
 */
public class NullStarterMonitor implements StarterMonitor {

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.configurer.startermonitor.StarterMonitor#done()
   */
  @Override
  public void done() {
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.configurer.startermonitor.StarterMonitor#nextStep
   * (java.lang.String)
   */
  @Override
  public void nextStep(final String message) {
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.configurer.startermonitor.StarterMonitor#
   * setEnvironment(org.projectsforge.swap.core.configurer.Environment)
   */
  @Override
  public void setEnvironment(final Environment environment) {
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.configurer.startermonitor.StarterMonitor#setMaxStep
   * (int)
   */
  @Override
  public void setMaxStep(final int maxStep) {
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.configurer.startermonitor.StarterMonitor#
   * updateStatus(java.lang.String)
   */
  @Override
  public void updateStatus(final String message) {
    // nothing to do
  }

}
