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
 * $Id: CssDescendantSelector.java 91 2011-07-18 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.selectors;

import org.projectsforge.swap.core.mime.css.nodes.Specificity;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

/**
 * The Class CssDescendantSelector.
 * 
 * @author Sébastien Aupetit
 */
public class CssDescendantSelector extends CssSelector implements DescendantSelector {

  /** The parent. */
  private final CssSelector parent;

  /** The descendant. */
  private final SimpleSelector descendant;

  /**
   * Instantiates a new css descendant selector.
   * 
   * @param parent
   *          the parent
   * @param descendant
   *          the descendant
   */
  public CssDescendantSelector(final CssSelector parent, final SimpleSelector descendant) {
    this.parent = parent;
    this.descendant = descendant;
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
    final CssDescendantSelector other = (CssDescendantSelector) obj;
    if (descendant == null) {
      if (other.descendant != null) {
        return false;
      }
    } else if (!descendant.equals(other.descendant)) {
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

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.Selector#getSelectorType()
   */
  @Override
  public short getSelectorType() {
    return Selector.SAC_DESCENDANT_SELECTOR;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DescendantSelector#getSimpleSelector()
   */
  @Override
  public SimpleSelector getSimpleSelector() {
    return descendant;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((descendant == null) ? 0 : descendant.hashCode());
    result = prime * result + ((parent == null) ? 0 : parent.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getAncestorSelector() + " " + getSimpleSelector();
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
    ((CssSelector) descendant).updateSpecificity(specificity);
  }
}
