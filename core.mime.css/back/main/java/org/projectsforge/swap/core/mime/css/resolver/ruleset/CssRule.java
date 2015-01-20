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
 * $Id: CssRule.java 98 2011-11-24 12:10:32Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.resolver.ruleset;

import org.projectsforge.swap.core.mime.css.nodes.Rule;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssSelector;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;

/**
 * The class representing the triple: a CSS selector, a CSS rule, the origin of
 * the rule.
 * 
 * @author Sébastien Aupetit
 */
public final class CssRule {

  /**
   * The Origin enumeration.
   */
  public enum Origin {
    /** From user agent default CSS styles. */
    USER_AGENT,
    /** From user default CSS styles. */
    USER,
    /** From author CSS styles. */
    AUTHOR,
    /** From author with style attribute. */
    AUTHOR_STYLE,
    /** From author with HTML tag. */
    HTMLTAG
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
   * @param element
   *          the element
   * @param selector
   *          the selector
   * @param rule
   *          the rule
   */
  public CssRule(final AbstractElement element, final CssSelector selector, final Rule rule) {
    this.selector = selector;
    this.rule = rule;
    this.origin = Origin.HTMLTAG;
    this.element = element;
    if (origin == null) {
      throw new IllegalArgumentException("origin must not be null");
    }
    if (selector == null) {
      throw new IllegalArgumentException("selector must not be null");
    }
  }

  /**
   * Instantiates a new css rule when origin is not equal to
   * {@link Origin#HTMLTAG}.
   * 
   * @param selector
   *          the selector
   * @param rule
   *          the rule
   * @param origin
   *          the origin
   */
  public CssRule(final CssSelector selector, final Rule rule, final Origin origin) {
    this.selector = selector;
    this.rule = rule;
    this.origin = origin;
    this.element = null;
    if (origin == Origin.HTMLTAG) {
      throw new IllegalArgumentException("This constructor must not be used for a HTML tag");
    }
    if (selector == null) {
      throw new IllegalArgumentException("selector must not be null");
    }
    if (rule == null) {
      throw new IllegalArgumentException("rule must not be null");
    }

  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof CssRule) {
      final CssRule other = (CssRule) obj;
      if (element != null) {
        if (!element.equals(other.element)) {
          return false;
        }
      } else if (other.element != null) {
        return false;
      }
      if (rule != null) {
        if (!rule.equals(other.rule)) {
          return false;
        }
      } else if (other.rule != null) {
        return false;
      }
      return origin.equals(other.origin) && rule.equals(other.rule)
          && selector.equals(other.selector);
    }
    return false;
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
    result = prime * result + ((origin == null) ? 0 : origin.hashCode());
    result = prime * result + ((rule == null) ? 0 : rule.hashCode());
    result = prime * result + ((selector == null) ? 0 : selector.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "[" + rule + " " + origin + " " + ((element != null) ? element.getName() : "") + "]";
  }

}
