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
 * $Id: FontFaceNode.java 91 2011-07-18 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.nodes;

import java.util.ArrayList;
import java.util.List;

/**
 * The class representing a {@code @fontface} CSS node.
 * 
 * @author Sébastien Aupetit
 */
public class FontFaceNode implements Element {

  /** The elements. */
  private final List<Element> elements = new ArrayList<Element>();

  /**
   * Adds an element.
   * 
   * @param element
   *          the element
   */
  public void add(final Element element) {
    elements.add(element);
  }

  /**
   * Gets the elements.
   * 
   * @return the elements
   */
  public List<Element> getElements() {
    return elements;
  }

  @Override
  public String toString() {
    return String.format("FontFaceNode [%s]", elements);
  }

}
