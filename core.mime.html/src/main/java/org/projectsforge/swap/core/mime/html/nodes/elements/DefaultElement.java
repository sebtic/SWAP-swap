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
package org.projectsforge.swap.core.mime.html.nodes.elements;

import org.projectsforge.utils.icasestring.ICaseString;

/**
 * The Class DefaultElement.
 * 
 * @author Sébastien Aupetit
 */
public class DefaultElement extends AbstractElement {

  /** The name. */
  private final ICaseString name;

  /**
   * The Constructor.
   * 
   * @param name the name
   */
  DefaultElement(final ICaseString name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement#getTagName
   * ()
   */
  @Override
  public ICaseString getTagName() {
    return name;
  }
}
