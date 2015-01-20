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
 * <http://www.gnu.org/licenses/>. $Id: CssElementSelector.java 91 2011-07-18
 * 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.selectors;

import org.projectsforge.swap.core.mime.css.nodes.Specificity;
import org.projectsforge.utils.icasestring.ICaseString;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

/**
 * The Class CssElementSelector.
 * 
 * @author Sébastien Aupetit
 */
public class CssElementSelector extends CssSelector implements ElementSelector {

  /** The name. */
  private final ICaseString name;

  /**
   * Instantiates a new css element selector.
   * 
   * @param name the name
   */
  public CssElementSelector(final ICaseString name) {
    this.name = name;
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
    final CssElementSelector other = (CssElementSelector) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.ElementSelector#getLocalName()
   */
  @Override
  public String getLocalName() {
    return (name != null) ? name.get() : null;
  }

  @Override
  public SimpleSelector getMainSelector() {
    return null;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public ICaseString getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.ElementSelector#getNamespaceURI()
   */
  @Override
  public String getNamespaceURI() {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.Selector#getSelectorType()
   */
  @Override
  public short getSelectorType() {
    return Selector.SAC_ELEMENT_NODE_SELECTOR;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final String name = getLocalName();
    if (name == null) {
      return "*";
    }
    return name;
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
    if (name != null) {
      specificity.addAnElementNameOrAPseudoElements();
    }
  }
}
