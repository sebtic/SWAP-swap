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
 * $Id: StarterMonitor.java 159 2012-10-10 11:52:07Z sebtic $
 */
package org.projectsforge.swap.core.environment;


/**
 * The interface used to monitor the startup of the application.
 * 
 * @author Sébastien Aupetit
 * 
 */
public interface StarterMonitor {

  /**
   * Starting is done.
   */
  void done();

  /**
   * Start the next major step.
   * 
   * @param message
   *          the message
   */
  void nextStep(String message);

  /**
   * Sets the environment.
   * 
   * @param environment
   *          the new environment
   */
  void setEnvironment(Environment environment);

  /**
   * Sets the max step.
   * 
   * @param maxStep
   *          the new max step
   */
  void setMaxStep(int maxStep);

  /**
   * Update the status.
   * 
   * @param message
   *          the message
   */
  void updateStatus(String message);
}
