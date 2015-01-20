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
 * $Id: CssRuleComparator.java 58 2010-09-23 19:23:09Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.resolver.ruleset;

import java.util.Comparator;

/**
 * The class used to compare two {@link CssRule} using specificities.
 * 
 * @author Sébastien Aupetit
 */
class CssRuleComparator implements Comparator<CssRule> {

  /*
   * (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(final CssRule o1, final CssRule o2) {
    return -o1.getSelector().getSpecificity().compare(o2.getSelector().getSpecificity());
  }
}
