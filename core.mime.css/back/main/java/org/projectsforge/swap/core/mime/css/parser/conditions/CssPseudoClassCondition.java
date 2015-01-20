/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SWAP. SWAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SWAP is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SWAP. If not, see
 * <http://www.gnu.org/licenses/>. $Id: CssPseudoClassCondition.java 84
 * 2011-06-09 10:09:03Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.conditions;

import org.projectsforge.swap.core.mime.css.nodes.Specificity;
import org.projectsforge.utils.icasestring.ICaseString;
import org.w3c.css.sac.Condition;

/**
 * The Class CssPseudoClassCondition.
 */
public class CssPseudoClassCondition extends AbstractCssAttributeCondition {

  /** The pseudo class name. */
  private final ICaseString pseudoClassName;

  /**
   * Instantiates a new css pseudo class condition.
   * 
   * @param pseudoClassName the pseudoClass name
   */
  public CssPseudoClassCondition(final String pseudoClassName) {
    super(null, pseudoClassName);
    this.pseudoClassName = new ICaseString(pseudoClassName);
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
    final CssPseudoClassCondition other = (CssPseudoClassCondition) obj;
    if (pseudoClassName == null) {
      if (other.pseudoClassName != null) {
        return false;
      }
    } else if (!pseudoClassName.equals(other.pseudoClassName)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.Condition#getConditionType()
   */
  @Override
  public short getConditionType() {
    return Condition.SAC_PSEUDO_CLASS_CONDITION;
  }

  /**
   * Gets the pseudo class name.
   * 
   * @return the pseudo class name
   */
  public ICaseString getPseudoClassName() {
    return pseudoClassName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((pseudoClassName == null) ? 0 : pseudoClassName.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return ":" + getValue();
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
    specificity.addAnOtherAttributeOrAPseudoClass();
  }
}
