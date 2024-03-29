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

import java.util.Map;
import org.projectsforge.swap.core.environment.Environment;

/**
 * The Interface AnnotatedClassesProvider is defined to allow custom detection
 * of annotated classes to be registered into a spring context.
 * 
 * @author Sébastien Aupetit
 */
public interface AnnotatedClassesProvider {

  /**
   * Gets the annotated classes.
   * 
   * @param environment
   *          the environment
   * @return the annotated classes
   */
  Map<String, Class<?>> getClasses(Environment environment);
}
