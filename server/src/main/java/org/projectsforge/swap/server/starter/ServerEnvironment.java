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
package org.projectsforge.swap.server.starter;

import org.projectsforge.swap.core.environment.impl.EnvironmentImpl;

/**
 * The server {@link EnvironmentImpl}.
 * 
 * @author Sébastien Aupetit
 */
public class ServerEnvironment extends EnvironmentImpl {

  /**
   * Instantiates a new server environment.
   * 
   * @param name
   *          the name
   */
  public ServerEnvironment(final String name) {
    super(name);
  }

}
