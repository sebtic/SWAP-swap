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
 * $Id: AbstractCssCombinatorCondition.java 83 2011-06-08 15:37:32Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.conditions;

import org.projectsforge.swap.core.mime.css.nodes.Specificity;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;

/**
 * The Class AbstractCssCombinatorCondition.
 * 
 * @author Sébastien Aupetit
 */
public abstract class AbstractCssCombinatorCondition implements CombinatorCondition, CssCondition {

  /** The first condition. */
  private final CssCondition firstCondition;

  /** The second condition. */
  private final CssCondition secondCondition;

  /**
   * Instantiates a new abstract css combinator condition.
   * 
   * @param first
   *          the first
   * @param second
   *          the second
   */
  public AbstractCssCombinatorCondition(final CssCondition first, final CssCondition second) {
    this.firstCondition = first;
    this.secondCondition = second;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final AbstractCssCombinatorCondition other = (AbstractCssCombinatorCondition) obj;
    if (firstCondition == null) {
      if (other.firstCondition != null) {
        return false;
      }
    } else if (!firstCondition.equals(other.firstCondition)) {
      return false;
    }
    if (secondCondition == null) {
      if (other.secondCondition != null) {
        return false;
      }
    } else if (!secondCondition.equals(other.secondCondition)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.CombinatorCondition#getFirstCondition()
   */
  @Override
  public Condition getFirstCondition() {
    return firstCondition;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.CombinatorCondition#getSecondCondition()
   */
  @Override
  public Condition getSecondCondition() {
    return secondCondition;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((firstCondition == null) ? 0 : firstCondition.hashCode());
    result = prime * result + ((secondCondition == null) ? 0 : secondCondition.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.transformations.css.parser.conditions.CssCondition
   * #updateSpecificity
   * (org.projectsforge.swap.transformations.css.nodes.Specificity)
   */
  @Override
  public void updateSpecificity(final Specificity specificity) {
    firstCondition.updateSpecificity(specificity);
    secondCondition.updateSpecificity(specificity);
  }
}
