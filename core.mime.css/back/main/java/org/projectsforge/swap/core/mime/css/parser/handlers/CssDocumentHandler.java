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
 * <http://www.gnu.org/licenses/>. $Id: CssDocumentHandler.java 91 2011-07-18
 * 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.projectsforge.swap.core.mime.css.nodes.Comment;
import org.projectsforge.swap.core.mime.css.nodes.FontFaceNode;
import org.projectsforge.swap.core.mime.css.nodes.ImportNode;
import org.projectsforge.swap.core.mime.css.nodes.Media;
import org.projectsforge.swap.core.mime.css.nodes.PageNode;
import org.projectsforge.swap.core.mime.css.nodes.Rule;
import org.projectsforge.swap.core.mime.css.nodes.SelectorRulesPair;
import org.projectsforge.swap.core.mime.css.nodes.Stylesheet;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssSelector;
import org.projectsforge.utils.icasestring.ICaseString;
import org.slf4j.Logger;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

/**
 * The document handler for the CSS parser.
 * 
 * @author Sébastien Aupetit
 */
public class CssDocumentHandler implements DocumentHandler {

  /** The logger. */
  private final Logger logger = org.slf4j.LoggerFactory.getLogger(CssDocumentHandler.class);

  /** The css. */
  private final Stylesheet css = new Stylesheet();

  /** The current medias. */
  private final List<Media> currentMedias = new ArrayList<Media>();

  /** The current selectors. */
  private List<CssSelector> currentSelectors = null;

  /** The current fontface. */
  private FontFaceNode currentFontface = null;

  /** The current page. */
  private PageNode currentPage = null;

  /** The is at font face. */
  private boolean isAtFontFace = false;

  /** The is at page. */
  private boolean isAtPage = false;

  /** The rules. */
  private List<Rule> rules = null;

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#comment(java.lang.String)
   */
  @Override
  public void comment(final String text) throws CSSException {
    for (final Media media : currentMedias) {
      media.add(new Comment(text));
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.DocumentHandler#endDocument(org.w3c.css.sac.InputSource)
   */
  @Override
  public void endDocument(final InputSource source) throws CSSException {
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#endFontFace()
   */
  @Override
  public void endFontFace() throws CSSException {
    isAtFontFace = false;
    for (final Media media : currentMedias) {
      media.add(currentFontface);
    }
    currentFontface = null;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#endMedia(org.w3c.css.sac.SACMediaList)
   */
  @Override
  public void endMedia(final SACMediaList media) throws CSSException {
    currentMedias.clear();
    currentMedias.add(getCSSStyleSheet().getMedia(Media.DEFAULT));
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#endPage(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void endPage(final String name, final String pseudoPage) throws CSSException {
    isAtPage = false;
    for (final Media media : currentMedias) {
      media.add(currentPage);
    }
    currentPage = null;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.DocumentHandler#endSelector(org.w3c.css.sac.SelectorList)
   */
  @Override
  public void endSelector(final SelectorList selectors) throws CSSException {
    if (isAtFontFace) { // @fontface
      for (final CssSelector selector : currentSelectors) {
        currentFontface.add(new SelectorRulesPair(selector, rules));
      }
    } else if (isAtPage) { // @page
      for (final CssSelector selector : currentSelectors) {
        currentPage.add(new SelectorRulesPair(selector, rules));
      }
    } else {
      for (final Media media : currentMedias) {
        for (final CssSelector selector : currentSelectors) {
          media.add(new SelectorRulesPair(selector, rules));
        }
      }
    }
    rules = null;
    currentSelectors = null;
  }

  /**
   * Gets the cSS style sheet.
   * 
   * @return the css
   */
  public Stylesheet getCSSStyleSheet() {
    return css;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#ignorableAtRule(java.lang.String)
   */
  @Override
  public void ignorableAtRule(final String atRule) throws CSSException {
    final String lower = atRule.toLowerCase();
    if (lower.startsWith("@charset ")) {
      // already handled before analysis
    } else {
      logger.warn("Ingored @ rule: {}", atRule);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#importStyle(java.lang.String,
   * org.w3c.css.sac.SACMediaList, java.lang.String)
   */
  @Override
  public void importStyle(final String uri, final SACMediaList media,
      final String defaultNamespaceURI) throws CSSException {
    // TODO verify that only @import at the beginning of the stylesheet are
    // taken into account
    final Set<ICaseString> importMedias = new HashSet<ICaseString>();
    for (int i = 0; i < media.getLength(); ++i) {
      importMedias.add(new ICaseString(media.item(i)));
    }
    if (media.getLength() == 0) {
      importMedias.add(Media.DEFAULT);
    }

    getCSSStyleSheet().addImport(new ImportNode(uri, importMedias));
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#namespaceDeclaration(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void namespaceDeclaration(final String prefix, final String uri) throws CSSException {
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#property(java.lang.String,
   * org.w3c.css.sac.LexicalUnit, boolean)
   */
  @Override
  public void property(final String name, final LexicalUnit value, final boolean important)
      throws CSSException {
    rules.add(new Rule(new ICaseString(name), value, important));
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.DocumentHandler#startDocument(org.w3c.css.sac.InputSource)
   */
  @Override
  public void startDocument(final InputSource source) throws CSSException {
    currentMedias.clear();
    currentMedias.add(getCSSStyleSheet().getMedia(Media.DEFAULT));
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#startFontFace()
   */
  @Override
  public void startFontFace() throws CSSException {
    isAtFontFace = true;
    currentFontface = new FontFaceNode();
    rules = new ArrayList<Rule>();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.DocumentHandler#startMedia(org.w3c.css.sac.SACMediaList)
   */
  @Override
  public void startMedia(final SACMediaList media) throws CSSException {
    currentMedias.clear();
    for (int i = 0; i < media.getLength(); ++i) {
      currentMedias.add(getCSSStyleSheet().getMedia(new ICaseString(media.item(i))));
    }
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.DocumentHandler#startPage(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void startPage(final String name, final String pseudoPage) throws CSSException {
    isAtPage = true;
    currentPage = new PageNode();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.DocumentHandler#startSelector(org.w3c.css.sac.SelectorList)
   */
  @Override
  public void startSelector(final SelectorList selectors) throws CSSException {
    currentSelectors = new ArrayList<CssSelector>();
    for (int i = 0; i < selectors.getLength(); ++i) {
      currentSelectors.add((CssSelector) selectors.item(i));
    }
    rules = new ArrayList<Rule>();
  }

}
