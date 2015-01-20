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
 * $Id: CssConditionalSelector.java 84 2011-06-09 10:09:03Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.selectors;

import org.projectsforge.swap.core.mime.css.nodes.Specificity;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

/**
 * The Class CssConditionalSelector.
 * 
 * @author Sébastien Aupetit
 */
public class CssConditionalSelector extends CssSelector implements ConditionalSelector {

  /** The selector. */
  private final CssElementSelector selector;

  /** The condition. */
  private final CssCondition condition;

  /**
   * Instantiates a new css conditional selector.
   * 
   * @param selector
   *          the selector
   * @param condition
   *          the condition
   */
  public CssConditionalSelector(final CssElementSelector selector, final CssCondition condition) {
    this.selector = selector;
    this.condition = condition;
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
    final CssConditionalSelector other = (CssConditionalSelector) obj;
    if (condition == null) {
      if (other.condition != null) {
        return false;
      }
    } else if (!condition.equals(other.condition)) {
      return false;
    }
    if (selector == null) {
      if (other.selector != null) {
        return false;
      }
    } else if (!selector.equals(other.selector)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.ConditionalSelector#getCondition()
   */
  @Override
  public Condition getCondition() {
    return condition;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.Selector#getSelectorType()
   */
  @Override
  public short getSelectorType() {
    return Selector.SAC_CONDITIONAL_SELECTOR;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.ConditionalSelector#getSimpleSelector()
   */
  @Override
  public SimpleSelector getSimpleSelector() {
    return selector;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((condition == null) ? 0 : condition.hashCode());
    result = prime * result + ((selector == null) ? 0 : selector.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.valueOf(getSimpleSelector()) + getCondition();
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
    specificity.addAnOtherAttributeOrAPseudoClass();
    condition.updateSpecificity(specificity);
  }
}
