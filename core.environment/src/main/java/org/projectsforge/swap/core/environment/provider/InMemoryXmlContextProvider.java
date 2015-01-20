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
package org.projectsforge.swap.core.environment.provider;

import java.util.Collection;
import org.projectsforge.swap.core.environment.Environment;
import org.xml.sax.InputSource;

/**
 * The Interface InMemoryXmlContextProvider is defined to allow the user to
 * provide custom in-memory XML configuration to be registered into a spring
 * context.
 * 
 * @author Sébastien Aupetit
 */
public interface InMemoryXmlContextProvider {

  /**
   * Gets the XML input sources.
   * 
   * @param environment
   *          the environment
   * @return the input sources
   */
  Collection<InputSource> getInputSources(Environment environment);

}
