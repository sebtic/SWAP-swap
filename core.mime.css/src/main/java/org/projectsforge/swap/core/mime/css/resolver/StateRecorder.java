/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.swap.core.mime.css.resolver;

import org.projectsforge.swap.core.mime.html.nodes.Text;

/**
 * The interface StateRecorder which allows to record the properties associated
 * with an element in a given state.
 * 
 * @author Sébastien Aupetit
 */
public interface StateRecorder {

  /**
   * Record the properties for an element in the given state.
   * 
   * @param resolverState the resolver state
   */
  void element(ResolverState resolverState);

  /**
   * Record the properties for a text.
   * 
   * @param parentResolverState the parent resolver state
   * @param text the text
   */
  void text(ResolverState parentResolverState, Text text);
}
