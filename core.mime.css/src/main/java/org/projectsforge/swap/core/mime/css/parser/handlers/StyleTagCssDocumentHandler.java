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
 * <http://www.gnu.org/licenses/>. $Id: StyleTagCssDocumentHandler.java 83
 * 2011-06-08 15:37:32Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.handlers;

import java.util.ArrayList;
import java.util.List;
import org.projectsforge.swap.core.mime.css.nodes.Rule;
import org.projectsforge.utils.icasestring.ICaseString;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

/**
 * The document handler for the CSS parser while parsing style tag.
 */
public class StyleTagCssDocumentHandler implements DocumentHandler {

  /** The rules. */
  List<Rule> rules = new ArrayList<Rule>();

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#comment(java.lang.String)
   */
  @Override
  public void comment(final String arg0) throws CSSException {
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.DocumentHandler#endDocument(org.w3c.css.sac.InputSource)
   */
  @Override
  public void endDocument(final InputSource arg0) throws CSSException {
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#endFontFace()
   */
  @Override
  public void endFontFace() throws CSSException {
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#endMedia(org.w3c.css.sac.SACMediaList)
   */
  @Override
  public void endMedia(final SACMediaList arg0) throws CSSException {
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#endPage(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void endPage(final String arg0, final String arg1) throws CSSException {
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.DocumentHandler#endSelector(org.w3c.css.sac.SelectorList)
   */
  @Override
  public void endSelector(final SelectorList arg0) throws CSSException {
  }

  /**
   * Gets the rules.
   * 
   * @return the rules
   */
  public List<Rule> getRules() {
    return rules;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#ignorableAtRule(java.lang.String)
   */
  @Override
  public void ignorableAtRule(final String arg0) throws CSSException {
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#importStyle(java.lang.String,
   * org.w3c.css.sac.SACMediaList, java.lang.String)
   */
  @Override
  public void importStyle(final String arg0, final SACMediaList arg1, final String arg2)
      throws CSSException {
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#namespaceDeclaration(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void namespaceDeclaration(final String arg0, final String arg1) throws CSSException {
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#property(java.lang.String,
   * org.w3c.css.sac.LexicalUnit, boolean)
   */
  @Override
  public void property(final String name, final LexicalUnit value, final boolean important)
      throws CSSException {
    // TODO a verifier
    rules.add(new Rule(new ICaseString(name), value, important));
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.DocumentHandler#startDocument(org.w3c.css.sac.InputSource)
   */
  @Override
  public void startDocument(final InputSource arg0) throws CSSException {
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#startFontFace()
   */
  @Override
  public void startFontFace() throws CSSException {
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.DocumentHandler#startMedia(org.w3c.css.sac.SACMediaList)
   */
  @Override
  public void startMedia(final SACMediaList arg0) throws CSSException {
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#startPage(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void startPage(final String arg0, final String arg1) throws CSSException {
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.DocumentHandler#startSelector(org.w3c.css.sac.SelectorList)
   */
  @Override
  public void startSelector(final SelectorList arg0) throws CSSException {
  }

}
