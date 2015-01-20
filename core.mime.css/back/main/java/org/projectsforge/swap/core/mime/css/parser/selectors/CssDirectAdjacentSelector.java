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
 * $Id: CssDirectAdjacentSelector.java 91 2011-07-18 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.selectors;

import org.projectsforge.swap.core.mime.css.nodes.Specificity;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

/**
 * The Class CssDirectAdjacentSelector.
 * 
 * @author Sébastien Aupetit
 */
public class CssDirectAdjacentSelector extends CssSelector implements SiblingSelector {

  /** The before. */
  private final CssSelector before;

  /** The after. */
  private final SimpleSelector after;

  /**
   * Instantiates a new css direct adjacent selector.
   * 
   * @param before
   *          the before
   * @param after
   *          the after
   */
  public CssDirectAdjacentSelector(final CssSelector before, final SimpleSelector after) {
    this.before = before;
    this.after = after;
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
    final CssDirectAdjacentSelector other = (CssDirectAdjacentSelector) obj;
    if (after == null) {
      if (other.after != null) {
        return false;
      }
    } else if (!after.equals(other.after)) {
      return false;
    }
    if (before == null) {
      if (other.before != null) {
        return false;
      }
    } else if (!before.equals(other.before)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.SiblingSelector#getNodeType()
   */
  @Override
  public short getNodeType() {
    return 1;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.SiblingSelector#getSelector()
   */
  @Override
  public Selector getSelector() {
    return before;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.Selector#getSelectorType()
   */
  @Override
  public short getSelectorType() {
    return Selector.SAC_DIRECT_ADJACENT_SELECTOR;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.SiblingSelector#getSiblingSelector()
   */
  @Override
  public SimpleSelector getSiblingSelector() {
    return after;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((after == null) ? 0 : after.hashCode());
    result = prime * result + ((before == null) ? 0 : before.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getSelector() + " + " + getSiblingSelector();
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
    before.updateSpecificity(specificity);
    ((CssSelector) after).updateSpecificity(specificity);
  }
}
