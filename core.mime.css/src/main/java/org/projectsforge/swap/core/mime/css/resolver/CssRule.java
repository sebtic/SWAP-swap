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
 * <http://www.gnu.org/licenses/>. $Id: CssRule.java 98 2011-11-24 12:10:32Z
 * sebtic $
 */
package org.projectsforge.swap.core.mime.css.resolver;

import org.projectsforge.swap.core.mime.css.nodes.Rule;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssSelector;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;

/**
 * The class representing the triple: a CSS selector, a CSS rule, the origin of
 * the rule.
 * 
 * @author Sébastien Aupetit
 */
public final class CssRule implements Comparable<CssRule> {

  /**
   * The Origin enumeration.
   * 
   * @author Sébastien Aupetit
   */
  public enum Origin {
    /** From user agent default CSS styles. */
    USER_AGENT,
    /** From user default CSS styles. */
    USER,
    /** From author CSS styles. */
    AUTHOR,
    /** From author with style attribute. */
    HTMLTAG_STYLE_PROPERTY,
    /** From author with HTML tag. */
    HTMLTAG_PROPERTY
  }

  /** The selector. */
  private final CssSelector selector;

  /** The rule. */
  private final Rule rule;

  /** The origin. */
  private final Origin origin;

  /** The element if there is one. */
  private final AbstractElement element;

  /**
   * Instantiates a new css rule for a HTML tag.
   * 
   * @param element the element
   * @param selector the selector
   * @param rule the rule
   * @param origin the origin
   */
  public CssRule(final AbstractElement element, final CssSelector selector, final Rule rule,
      final Origin origin) {
    this.selector = selector;
    this.rule = rule;
    this.origin = origin;
    this.element = element;

    if (origin == null) {
      throw new IllegalArgumentException("origin must not be null");
    }

    if (origin == Origin.HTMLTAG_STYLE_PROPERTY || origin == Origin.HTMLTAG_PROPERTY) {
      if (element == null) {
        throw new IllegalArgumentException("element must not be null with origin " + origin);
      }
    } else if (element != null) {
      throw new IllegalArgumentException("element must be null with origin " + origin);
    }

    if (selector == null) {
      throw new IllegalArgumentException("selector must not be null");
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final CssRule other) {
    // More specific rule are at the end
    return -getSelector().getSpecificity().compare(other.getSelector().getSpecificity());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CssRule)) {
      return false;
    }
    final CssRule other = (CssRule) obj;
    if (element != other.element) {
      return false;
    }
    if (origin != other.origin) {
      return false;
    }
    if (rule == null) {
      if (other.rule != null) {
        return false;
      }
    } else if (!rule.equals(other.rule)) {
      return false;
    }
    if (selector == null) {
      if (other.selector != null) {
        return false;
      }
    } else if (!selector.equals(other.selector)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the element.
   * 
   * @return the element
   */
  public AbstractElement getElement() {
    return element;
  }

  /**
   * Gets the origin.
   * 
   * @return the origin
   */
  public Origin getOrigin() {
    return origin;
  }

  /**
   * Gets the rule.
   * 
   * @return the rule
   */
  public Rule getRule() {
    return rule;
  }

  /**
   * Gets the selector.
   * 
   * @return the selector
   */
  public CssSelector getSelector() {
    return selector;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((element == null) ? 0 : 1);
    result = prime * result + ((origin == null) ? 0 : origin.hashCode());
    result = prime * result + ((rule == null) ? 0 : rule.hashCode());
    result = prime * result + ((selector == null) ? 0 : selector.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + rule + " " + origin + " " + ((element != null) ? element.getTagName() : "") + "]";

  }
}
