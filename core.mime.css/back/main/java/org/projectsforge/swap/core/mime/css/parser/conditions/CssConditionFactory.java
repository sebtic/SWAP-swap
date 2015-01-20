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
 * <http://www.gnu.org/licenses/>. $Id: CssConditionFactory.java 83 2011-06-08
 * 15:37:32Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.conditions;

import org.projectsforge.utils.icasestring.ICaseString;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.ContentCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.PositionalCondition;

/**
 * A factory for creating CssCondition objects.
 * 
 * @author Sébastien Aupetit
 */
public class CssConditionFactory implements ConditionFactory {

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.ConditionFactory#createAndCondition(org.w3c.css.sac.Condition
   * , org.w3c.css.sac.Condition)
   */
  @Override
  public CombinatorCondition createAndCondition(final Condition first, final Condition second)
      throws CSSException {
    return new CssAndCondition((CssCondition) first, (CssCondition) second);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.ConditionFactory#createAttributeCondition(java.lang.String,
   * java.lang.String, boolean, java.lang.String)
   */
  @Override
  public AttributeCondition createAttributeCondition(final String localName,
      final String namespaceURI, final boolean specified, final String value) throws CSSException {
    return new CssAttributeCondition(new ICaseString(localName), value);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.ConditionFactory#createBeginHyphenAttributeCondition(java
   * .lang.String, java.lang.String, boolean, java.lang.String)
   */
  @Override
  public AttributeCondition createBeginHyphenAttributeCondition(final String localName,
      final String namespaceURI, final boolean specified, final String value) throws CSSException {
    return new CssBeginHyphenAttributeCondition(new ICaseString(localName), value);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.ConditionFactory#createClassCondition(java.lang.String,
   * java.lang.String)
   */
  @Override
  public AttributeCondition createClassCondition(final String namespaceURI, final String value)
      throws CSSException {
    return new CssClassCondition(value);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.ConditionFactory#createContentCondition(java.lang.String)
   */
  @Override
  public ContentCondition createContentCondition(final String data) throws CSSException {
    throw new CSSException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.ConditionFactory#createIdCondition(java.lang.String)
   */
  @Override
  public AttributeCondition createIdCondition(final String value) throws CSSException {
    return new CssIdCondition(value);
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.ConditionFactory#createLangCondition(java.lang.String)
   */
  @Override
  public LangCondition createLangCondition(final String lang) throws CSSException {
    return new CssLangCondition(new ICaseString(lang));
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.ConditionFactory#createNegativeCondition(org.w3c.css.sac
   * .Condition)
   */
  @Override
  public NegativeCondition createNegativeCondition(final Condition condition) throws CSSException {
    throw new CSSException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.ConditionFactory#createOneOfAttributeCondition(java.lang
   * .String, java.lang.String, boolean, java.lang.String)
   */
  @Override
  public AttributeCondition createOneOfAttributeCondition(final String localName,
      final String namespaceURI, final boolean specified, final String value) throws CSSException {
    return new CssOneOfAttributeCondition(new ICaseString(localName), value);
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.ConditionFactory#createOnlyChildCondition()
   */
  @Override
  public Condition createOnlyChildCondition() throws CSSException {
    throw new CSSException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.ConditionFactory#createOnlyTypeCondition()
   */
  @Override
  public Condition createOnlyTypeCondition() throws CSSException {
    throw new CSSException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.ConditionFactory#createOrCondition(org.w3c.css.sac.Condition
   * , org.w3c.css.sac.Condition)
   */
  @Override
  public CombinatorCondition createOrCondition(final Condition first, final Condition second)
      throws CSSException {
    throw new CSSException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.ConditionFactory#createPositionalCondition(int,
   * boolean, boolean)
   */
  @Override
  public PositionalCondition createPositionalCondition(final int position, final boolean typeNode,
      final boolean type) throws CSSException {
    throw new CSSException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.ConditionFactory#createPseudoClassCondition(java.lang.String
   * , java.lang.String)
   */
  @Override
  public AttributeCondition createPseudoClassCondition(final String namespaceURI, final String value)
      throws CSSException {
    return new CssPseudoClassCondition(value);
  }
}
