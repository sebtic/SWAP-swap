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
 * $Id: CssAndCondition.java 83 2011-06-08 15:37:32Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.conditions;

import org.w3c.css.sac.Condition;

/**
 * The Class CssAndCondition.
 * 
 * @author Sébastien Aupetit
 */
public class CssAndCondition extends AbstractCssCombinatorCondition {

  /**
   * Instantiates a new css and condition.
   * 
   * @param first
   *          the first
   * @param second
   *          the second
   */
  public CssAndCondition(final CssCondition first, final CssCondition second) {
    super(first, second);
  }

  @Override
  public short getConditionType() {
    return Condition.SAC_AND_CONDITION;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.valueOf(getFirstCondition()) + getSecondCondition();
  }
}
