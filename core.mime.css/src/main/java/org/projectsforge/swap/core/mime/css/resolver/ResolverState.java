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
package org.projectsforge.swap.core.mime.css.resolver;

import org.projectsforge.swap.core.mime.html.nodes.elements.AElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.BODYElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.HTMLElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.INPUTElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.OPTIONElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.TEXTAREAElement;

/**
 * The Class ResolverState.
 * 
 * @author Sébastien Aupetit
 */
public class ResolverState {

  /** The element. */
  private final AbstractElement element;

  /** The element state. */
  private final State elementState;

  /** The element properties. */
  private final Properties elementProperties = new Properties();

  /** The parent resolver state. */
  private final ResolverState parentResolverState;

  /** Indicate if we are descending of the body tag */
  private final boolean inBodyTagBranch;;

  /**
   * Instantiates a new resolver state.
   * 
   * @param element the element
   * @param elementState the element state
   * @param parentResolverState the parent resolver state
   */
  public ResolverState(final AbstractElement element, final State elementState,
      final ResolverState parentResolverState) {
    this.element = element;
    this.elementState = elementState;
    this.parentResolverState = parentResolverState;
    this.inBodyTagBranch = element instanceof BODYElement
        || (parentResolverState != null && parentResolverState.inBodyTagBranch);
  }

  /**
   * Gets the element.
   * 
   * @return the element
   */
  public AbstractElement getElement() {
    return element;
  }

  /**
   * Gets the element properties.
   * 
   * @return the element properties
   */
  public Properties getElementProperties() {
    return elementProperties;
  }

  /**
   * Gets the element state.
   * 
   * @return the element state
   */
  public State getElementState() {
    return elementState;
  }

  /**
   * Gets the parent resolver state.
   * 
   * @return the parent resolver state
   */
  public ResolverState getParentResolverState() {
    return parentResolverState;
  }

  /**
   * Checks for valid state.
   * 
   * @return true, if successful
   */
  boolean hasValidState() {
    if (element instanceof HTMLElement || element instanceof BODYElement) {
      if (elementState.active || elementState.focused || elementState.hovered
          || elementState.visited) {
        return false;
      } else {
        return true;
      }
    }

    // visited is valid only on <a> tag
    if (!(element instanceof AElement) && elementState.visited) {
      return false;
    }
    // active is valid only on <a> tag
    if (elementState.active && !(element instanceof AElement)) {
      return false;
    }

    if (elementState.focused) {
      // it must accept keyboard input
      if (!(element instanceof INPUTElement || element instanceof OPTIONElement || element instanceof TEXTAREAElement)) {
        return false;
      }
    }

    if (elementState.focused) {
      // Simplistic hypothesis: only one element can be focused at a time
      ResolverState rs = parentResolverState;
      while (rs != null) {
        if (rs.elementState.focused) {
          return false;
        }
        rs = rs.parentResolverState;
      }
    }
    if (elementState.hovered) {
      // Simplistic hypothesis: only one element can be focused at a time
      ResolverState rs = parentResolverState;
      while (rs != null) {
        if (rs.elementState.hovered) {
          return false;
        }
        rs = rs.parentResolverState;
      }
    }
    return true;
  }

  public boolean isInBodyTagBranch() {
    return inBodyTagBranch;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "" + element + "[" + elementState + "," + elementProperties + "] -> "
        + parentResolverState;
  }
}
