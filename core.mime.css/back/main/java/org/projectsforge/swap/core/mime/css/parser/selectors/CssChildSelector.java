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
 * $Id: CssChildSelector.java 91 2011-07-18 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.selectors;

import org.projectsforge.swap.core.mime.css.nodes.Specificity;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

/**
 * The Class CssChildSelector.
 * 
 * @author Sébastien Aupetit
 */
public class CssChildSelector extends CssSelector implements DescendantSelector {

  /** The parent. */
  private final CssSelector parent;

  /** The child. */
  private final SimpleSelector child;

  /**
   * Instantiates a new css child selector.
   * 
   * @param parent
   *          the parent
   * @param child
   *          the child
   */
  public CssChildSelector(final CssSelector parent, final SimpleSelector child) {
    this.parent = parent;
    this.child = child;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CssChildSelector other = (CssChildSelector) obj;
    if (child == null) {
      if (other.child != null) {
        return false;
      }
    } else if (!child.equals(other.child)) {
      return false;
    }
    if (parent == null) {
      if (other.parent != null) {
        return false;
      }
    } else if (!parent.equals(other.parent)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DescendantSelector#getAncestorSelector()
   */
  @Override
  public Selector getAncestorSelector() {
    return parent;
  }

  /**
   * Gets the child.
   * 
   * @return the child
   */
  public SimpleSelector getChild() {
    return child;
  }

  /**
   * Gets the parent.
   * 
   * @return the parent
   */
  public Selector getParent() {
    return parent;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.Selector#getSelectorType()
   */
  @Override
  public short getSelectorType() {
    return Selector.SAC_CHILD_SELECTOR;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DescendantSelector#getSimpleSelector()
   */
  @Override
  public SimpleSelector getSimpleSelector() {
    return child;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((child == null) ? 0 : child.hashCode());
    result = prime * result + ((parent == null) ? 0 : parent.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final SimpleSelector s = getSimpleSelector();
    if (s.getSelectorType() == Selector.SAC_PSEUDO_ELEMENT_SELECTOR) {
      return String.valueOf(getAncestorSelector()) + s;
    }
    return getAncestorSelector() + " > " + s;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.transformations.css.parser.selectors.CssSelector
   * #updateSpecificity
   * (org.projectsforge.swap.transformations.css.nodes.Specificity)
   */
  @Override
  protected void updateSpecificity(final Specificity specificity) {
    parent.updateSpecificity(specificity);
    ((CssSelector) child).updateSpecificity(specificity);
  }
}
