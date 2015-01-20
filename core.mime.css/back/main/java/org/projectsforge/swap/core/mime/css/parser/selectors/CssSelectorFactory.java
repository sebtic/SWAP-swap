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
 * <http://www.gnu.org/licenses/>. $Id: CssSelectorFactory.java 91 2011-07-18
 * 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.selectors;

import org.projectsforge.swap.core.mime.css.parser.conditions.CssCondition;
import org.projectsforge.utils.icasestring.ICaseString;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CharacterDataSelector;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.ProcessingInstructionSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

/**
 * A factory for creating CssSelector objects.
 * 
 * @author Sébastien Aupetit
 */
public class CssSelectorFactory implements SelectorFactory {

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.SelectorFactory#createAnyNodeSelector()
   */
  @Override
  public SimpleSelector createAnyNodeSelector() throws CSSException {
    throw new CSSException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.SelectorFactory#createCDataSectionSelector(java.lang.String
   * )
   */
  @Override
  public CharacterDataSelector createCDataSectionSelector(final String data) throws CSSException {
    throw new CSSException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.SelectorFactory#createChildSelector(org.w3c.css.sac.Selector
   * , org.w3c.css.sac.SimpleSelector)
   */
  @Override
  public DescendantSelector createChildSelector(final Selector parent, final SimpleSelector child)
      throws CSSException {
    return new CssChildSelector((CssSelector) parent, child);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.SelectorFactory#createCommentSelector(java.lang.String)
   */
  @Override
  public CharacterDataSelector createCommentSelector(final String data) throws CSSException {
    throw new CSSException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.SelectorFactory#createConditionalSelector(org.w3c.css.sac
   * .SimpleSelector, org.w3c.css.sac.Condition)
   */
  @Override
  public ConditionalSelector createConditionalSelector(final SimpleSelector selector,
      final Condition condition) throws CSSException {
    return new CssConditionalSelector((CssElementSelector) selector, (CssCondition) condition);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.SelectorFactory#createDescendantSelector(org.w3c.css.sac
   * .Selector, org.w3c.css.sac.SimpleSelector)
   */
  @Override
  public DescendantSelector createDescendantSelector(final Selector parent,
      final SimpleSelector descendant) throws CSSException {
    return new CssDescendantSelector((CssSelector) parent, descendant);
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.SelectorFactory#createDirectAdjacentSelector(short,
   * org.w3c.css.sac.Selector, org.w3c.css.sac.SimpleSelector)
   */
  @Override
  public SiblingSelector createDirectAdjacentSelector(final short nodeType, final Selector child,
      final SimpleSelector directAdjacent) throws CSSException {
    return new CssDirectAdjacentSelector((CssSelector) child, directAdjacent);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.SelectorFactory#createElementSelector(java.lang.String,
   * java.lang.String)
   */
  @Override
  public ElementSelector createElementSelector(final String namespaceURI, final String tagName)
      throws CSSException {
    if (tagName == null) {
      return new CssElementSelector(null);
    } else {
      return new CssElementSelector(new ICaseString(tagName));
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.SelectorFactory#createNegativeSelector(org.w3c.css.sac.
   * SimpleSelector)
   */
  @Override
  public NegativeSelector createNegativeSelector(final SimpleSelector selector) throws CSSException {
    throw new CSSException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.SelectorFactory#createProcessingInstructionSelector(java
   * .lang.String, java.lang.String)
   */
  @Override
  public ProcessingInstructionSelector createProcessingInstructionSelector(final String target,
      final String data) throws CSSException {
    throw new CSSException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.SelectorFactory#createPseudoElementSelector(java.lang.String
   * , java.lang.String)
   */
  @Override
  public ElementSelector createPseudoElementSelector(final String namespaceURI,
      final String pseudoName) throws CSSException {
    return new CssPseudoElementSelector(new ICaseString(pseudoName));
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.SelectorFactory#createRootNodeSelector()
   */
  @Override
  public SimpleSelector createRootNodeSelector() throws CSSException {
    throw new CSSException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.SelectorFactory#createTextNodeSelector(java.lang.String)
   */
  @Override
  public CharacterDataSelector createTextNodeSelector(final String data) throws CSSException {
    throw new CSSException("Not implemented");
  }

}
