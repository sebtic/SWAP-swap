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
 * <http://www.gnu.org/licenses/>. $Id: CssPseudoElementSelector.java 84
 * 2011-06-09 10:09:03Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.selectors;

import org.projectsforge.swap.core.mime.css.nodes.Specificity;
import org.projectsforge.utils.icasestring.ICaseString;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.Selector;

/**
 * The Class CssPseudoElementSelector.
 * 
 * @author Sébastien Aupetit
 */
public class CssPseudoElementSelector extends CssElementSelector implements ElementSelector {

  /** The name. */
  private final ICaseString pseudoName;

  /**
   * Instantiates a new css pseudo element selector.
   * 
   * @param pseudoName the pseudo name
   */
  public CssPseudoElementSelector(final ICaseString pseudoName) {
    super(pseudoName);
    this.pseudoName = pseudoName;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.transformations.css.parser.selectors.CssElementSelector
   * #getLocalName()
   */
  @Override
  public String getLocalName() {
    return pseudoName.get();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.transformations.css.parser.selectors.CssElementSelector
   * #getNamespaceURI()
   */
  @Override
  public String getNamespaceURI() {
    return null;
  }

  /**
   * Gets the pseudo element.
   * 
   * @return the pseudo element
   */
  public ICaseString getPseudoElement() {
    return pseudoName;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.transformations.css.parser.selectors.CssElementSelector
   * #getSelectorType()
   */
  @Override
  public short getSelectorType() {
    return Selector.SAC_PSEUDO_ELEMENT_SELECTOR;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.transformations.css.parser.selectors.CssElementSelector
   * #toString()
   */
  @Override
  public String toString() {
    return ":" + getLocalName();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.transformations.css.parser.selectors.CssElementSelector
   * #
   * updateSpecificity(org.projectsforge.swap.transformations.css.nodes.Specificity
   * )
   */
  @Override
  protected void updateSpecificity(final Specificity specificity) {
    specificity.addAnElementNameOrAPseudoElements();
  }
}
