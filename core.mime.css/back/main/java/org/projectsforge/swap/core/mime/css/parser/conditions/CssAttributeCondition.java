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
 * <http://www.gnu.org/licenses/>. $Id: CssAttributeCondition.java 84 2011-06-09
 * 10:09:03Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.conditions;

import org.projectsforge.swap.core.mime.css.nodes.Specificity;
import org.projectsforge.utils.icasestring.ICaseString;
import org.w3c.css.sac.Condition;

/**
 * The Class CssAttributeCondition representing conditions of the forme
 * [attribute="value"].
 * 
 * @author Sébastien Aupetit
 */
public class CssAttributeCondition extends AbstractCssAttributeCondition {

  /**
   * Instantiates a new css attribute condition.
   * 
   * @param localName the local name
   * @param value the value
   */
  public CssAttributeCondition(final ICaseString localName, final String value) {
    super(localName, value);
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.Condition#getConditionType()
   */
  @Override
  public short getConditionType() {
    return Condition.SAC_ATTRIBUTE_CONDITION;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (getValue() == null) {
      return "[" + getLocalNameCaseInsensitive() + "]";
    }
    return "[" + getLocalNameCaseInsensitive() + "=\"" + getValue() + "\"]";
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
