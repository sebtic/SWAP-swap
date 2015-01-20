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
 * <http://www.gnu.org/licenses/>. $Id: CssSelector.java 83 2011-06-08 15:37:32Z
 * sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.selectors;

import org.projectsforge.swap.core.mime.css.nodes.Specificity;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

/**
 * The Class CssSelector.
 * 
 * @author Sébastien Aupetit
 */
public abstract class CssSelector implements Selector {

  /** The specificity. */
  private Specificity specificity = null;

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
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
    final CssSelector other = (CssSelector) obj;
    if (specificity == null) {
      if (other.specificity != null) {
        return false;
      }
    } else if (!specificity.equals(other.specificity)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the main selector.
   * 
   * @return the main selector
   */
  public abstract SimpleSelector getMainSelector();

  /**
   * Gets the specificity.
   * 
   * @return the specificity
   */
  public Specificity getSpecificity() {
    if (specificity == null) {
      final Specificity spec = new Specificity();
      updateSpecificity(spec);
      specificity = spec;
    }
    return specificity;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((specificity == null) ? 0 : specificity.hashCode());
    return result;
  }

  /**
   * Update specificity.
   * 
   * @param specificity the specificity
   */
  protected abstract void updateSpecificity(Specificity specificity);
}
