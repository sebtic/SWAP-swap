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
 * <http://www.gnu.org/licenses/>. $Id: CssDummySelector.java 83 2011-06-08
 * 15:37:32Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.selectors;

import org.projectsforge.swap.core.mime.css.nodes.Specificity;
import org.w3c.css.sac.SimpleSelector;

/**
 * The Class CssDummySelector.
 * 
 * @author Sébastien Aupetit
 */
public class CssDummySelector extends CssSelector {

  /** The instance. */
  private static CssDummySelector instance;

  /**
   * Gets the single instance of CssDummySelector.
   * 
   * @return single instance of CssDummySelector
   */
  public static synchronized CssDummySelector getInstance() {
    if (instance == null) {
      instance = new CssDummySelector();
    }
    return instance;
  }

  /**
   * Instantiates a new css dummy selector.
   */
  private CssDummySelector() {
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.parser.selectors.CssSelector#equals
   * (java.lang.Object)
   */
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
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.parser.selectors.CssSelector#
   * getMainSelector()
   */
  @Override
  public SimpleSelector getMainSelector() {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.Selector#getSelectorType()
   */
  @Override
  public short getSelectorType() {
    return -1;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.transformations.css.parser.selectors.CssSelector
   * #updateSpecificity
   * (org.projectsforge.swap.transformations.css.nodes.Specificity)
   */
  @Override
  public void updateSpecificity(final Specificity specificity) {
    specificity.setStyleElement(true);
  }
}
